package app.graphql

import java.time.ZonedDateTime

import app.graphql.AuthMiddleware.{AuthenticationException, AuthorizationException}
import app.{AppContext, DBSchema}
import sangria.schema._
import sangria.execution.deferred._
import sangria.macros.derive._
import app.Models._
import org.joda.time.DateTime
import sangria.ast.{ListValue, StringValue}
import sangria.execution.{ExceptionHandler, FieldTag, HandledException}
import sangria.marshalling.{CoercedScalaResultMarshaller, FromInput}
import sangria.marshalling.queryAst._
import sangria.validation.Violation
import app.graphql.GraphQLHelpers._
import sangria.macros.derive

import scala.collection.immutable.ListMap
import scala.util.Try

object GraphQLSchema {
  def apply(): Schema[AppContext, Unit] = schema

  /*
   * Common
   */

  implicit val IdentifiableWithStringType = InterfaceType(
    "IdentifiableWithString",
    "Entity that can be identified with String",
    fields[AppContext, Identifiable[String]](Field("id", StringType, resolve = _.value.id)))

  implicit val IdentifiableWithIntType = InterfaceType(
    "IdentifiableWithInt",
    "Entity that can be identified with Integer",
    fields[AppContext, Identifiable[Int]](Field("id", IntType, resolve = _.value.id)))

  /*
   * Types
   */

  implicit val UserRoleType = deriveEnumType[UserRole.Value](EnumTypeName("UserRole"),
    IncludeValues("Admin", "Fairy", "Client", "WorkshopManager", "WarehouseManager"))

  implicit val UserType = deriveObjectType[AppContext, User](
    Interfaces(IdentifiableWithStringType),
    ObjectTypeName("User"),
    ObjectTypeDescription("User account and info"),
    ExcludeFields("password"))

  implicit val KnowledgeKindType = deriveEnumType[KnowledgeKind.Value](EnumTypeName("KnowledgeKind"),
    IncludeValues("Gossip", "Book", "Myth", "Fable"))
  implicit val KnowledgeType = deriveObjectType[AppContext, Knowledge](
    Interfaces(IdentifiableWithIntType),
    ObjectTypeName("Knowledge"))

  implicit val IngredientType = deriveObjectType[AppContext, Ingredient](
    Interfaces(IdentifiableWithIntType),
    ObjectTypeName("Ingredient"))

  implicit val IngredientRequestType = deriveObjectType[AppContext, IngredientRequest](
    Interfaces(IdentifiableWithIntType),
    ObjectTypeName("IngredientRequest"),
    ReplaceField("ingredients", Field("ingredients", ListType(IngredientType),
      resolve = c => ingredientFetcher.deferSeq(c.value.ingredients))))

  implicit val RecipeType = deriveObjectType[AppContext, Recipe](
    Interfaces(IdentifiableWithIntType),
    ObjectTypeName("Recipe"),
    ReplaceField("ingredients", Field("ingredients", ListType(IngredientType),
      resolve = c => ingredientFetcher.deferSeq(c.value.ingredients))))

  implicit val ProductType = deriveObjectType[AppContext, Product](
    Interfaces(IdentifiableWithIntType),
    ObjectTypeName("Product"),
    ReplaceField("tags", Field("tags", ListType(StringType),
      resolve = c => c.value.tags.asInstanceOf[List[String]])),
    ReplaceField("recipe", Field("recipe", RecipeType,
      resolve = c => recipeFetcher.defer(c.value.recipe))))

  implicit val OrderType = deriveObjectType[AppContext, Order](
    Interfaces(IdentifiableWithIntType),
    ObjectTypeName("Order"),
    ReplaceField("product", Field("product", ProductType,
      resolve = c => productFetcher.defer(c.value.product))))

  implicit val ProductTransferStatusType =
    deriveEnumType[ProductTransferStatus.Value](EnumTypeName("ProductTransferStatus"),
      IncludeValues("Produced", "Transferred", "Stored"))
  implicit val ProductTransferType = deriveObjectType[AppContext, ProductTransfer](
    Interfaces(IdentifiableWithIntType),
    ObjectTypeName("ProductTransfer"),
    ReplaceField("products", Field("products", ListType(ProductType),
      resolve = c => productFetcher.deferSeq(c.value.products))))

  /*
  * Access tags
  **/

  sealed trait RoleTag extends FieldTag {
    def check(u: Option[User]): Boolean
    def check(u: Option[User], ctx: AppContext): Boolean = check(u)
  }

  case object AuthenticatedTag extends RoleTag {def check(u: Option[User]) = u.nonEmpty}
  case object NotAuthenticatedTag extends RoleTag {def check(u: Option[User]) = u.isEmpty}
  case object ClientTag extends RoleTag {def check(u: Option[User]) = u.exists(_.role == UserRole.Client)}
  case object WorkerTag extends RoleTag {def check(u: Option[User]) = u.exists(_.role != UserRole.Client)}
  case object FairyTag extends RoleTag {def check(u: Option[User]) = u.exists(_.role == UserRole.Fairy)}
  case object AdminManagerTag extends RoleTag {def check(u: Option[User]) = u.exists(_.role == UserRole.Admin)}
  case object WorkshopManagerTag extends RoleTag {def check(u: Option[User]) = u.exists(_.role == UserRole.WorkshopManager)}
  case object WarehouseManagerTag extends RoleTag {def check(u: Option[User]) = u.exists(_.role == UserRole.WarehouseManager)}


  /*
  * Arguments
  */

  val AOrder = Argument("order",
    deriveInputObjectType[Order](InputObjectTypeName("OrderArg"),
      ExcludeInputFields("id")))


  val ARequest = Argument("request",
    deriveInputObjectType[IngredientRequest](InputObjectTypeName("RequestArg"),
      ExcludeInputFields("id"),
      ReplaceInputField("ingredients", InputField("ingredients", ListInputType(IntType)))))

  val ACredentials = Argument("credentials",
    deriveInputObjectType[User.Credentials](InputObjectTypeName("Credentials")))

  val IdStr = Argument("id", StringType)
  val IdInt = Argument("id", IntType)
  val SearchString = Argument("string", StringType)
  val SearchLimit = Argument("limit", IntType)
  val SearchLookaround = Argument("lookaround", IntType)

  val AProducts = Argument("products", ListInputType(IntType))
  val APassword = Argument("password", StringType)

  /*
  * Queries and Mutations
  */

  /*
   * UseCases
   * 1. Order Creation (3.1.4)
   * 2. Search Literature (3.1.7)
   * 3. Ingredients Request (3.1.10)
   * 4. "Potion produced" 3.1.13
   */

  val QueryType = ObjectType("Query", "Schema Queries",
    fields[AppContext, Unit](
      Field("loggedIn", BooleanType,
        resolve = c => c.ctx.currentUser.nonEmpty),
      Field("currentUser", OptionType(UserType),
        resolve = c => c.ctx.currentUser,
        tags = AuthenticatedTag :: Nil
      ),
      Field("user", OptionType(UserType),
        arguments = IdStr :: Nil,
        resolve = c => c.ctx.dao.getUser(c.arg(IdStr)),
        tags = Nil
      ),
      Field("searchKnowledge", ListType(KnowledgeType),
        arguments = SearchString :: SearchLimit :: SearchLookaround :: Nil,
        resolve = c => c.ctx.dao.searchKnowledge(c.arg(SearchString), c.arg(SearchLimit), c.arg(SearchLookaround)),
        tags = FairyTag :: Nil),
      Field("getKnowledge", KnowledgeType,
        arguments = IdInt :: Nil,
        resolve = c => c.ctx.dao.getKnowledge(c.arg(IdInt))),
      Field("ingredient", IngredientType,
        arguments = IdInt :: Nil,
        resolve = c => ingredientFetcher.defer(c.arg(IdInt)),
        tags = WorkerTag :: Nil),
      Field("allIngredients", ListType(IngredientType),
        resolve = c => c.ctx.dao.getAllIngredients,
        tags = WorkerTag :: Nil),
      Field("product", ProductType,
        arguments = IdInt :: Nil,
        resolve = c => DeferredValue(productFetcher.defer(c.arg(IdInt)))),
      Field("allProducts", ListType(ProductType),
        resolve = c => c.ctx.dao.getAllProducts),
    ))

  val MutationType = ObjectType("Mutation", "Schema Mutations",
    fields[AppContext, Unit](
      Field("login",
        OptionType(UserType),
        arguments = ACredentials :: Nil,
        resolve = c =>
          UpdateCtx(c.ctx.login(c.arg(ACredentials))){
            case Some(user) => c.ctx.copy(currentUser = Some(user))
            case None => c.ctx
          },
        tags = NotAuthenticatedTag :: Nil
      ),
      Field("logout",
        BooleanType,
        resolve = c =>
          UpdateCtx(c.ctx.logout()){
            if (_) c.ctx.copy(currentUser = None)
            else c.ctx
          }
      ),
      Field("createOrder", IntType,
        arguments = AOrder :: Nil,
        resolve = c => c.ctx.dao.create(c.arg(AOrder)),
        tags = ClientTag :: Nil),
      Field("requestIngredient", IntType,
        arguments = ARequest :: Nil,
        resolve = c => c.ctx.dao.create(c.arg(ARequest)),
        tags = WorkshopManagerTag :: Nil),
      Field("makeReport", IntType,
        arguments = AProducts :: Nil,
        resolve = c => c.ctx.dao.create(ProductTransfer(products = c.arg(AProducts))),
        tags = WorkshopManagerTag :: Nil)
    ))


  //  val userFetcher: Fetcher[AppContext, User, User, Int] =
  //    Fetcher((ctx: AppContext, ids: Seq[Int]) => ctx.dao.getUsers(ids))
  val recipeFetcher: Fetcher[AppContext, Recipe, Recipe, Int] =
  Fetcher((ctx: AppContext, ids: Seq[Int]) => ctx.dao.getRecipes(ids))
  val ingredientFetcher: Fetcher[AppContext, Ingredient, Ingredient, Int] =
    Fetcher((ctx: AppContext, ids: Seq[Int]) => ctx.dao.getIngredients(ids))
  val productFetcher: Fetcher[AppContext, Product, Product, Int] =
    Fetcher((ctx: AppContext, ids: Seq[Int]) => ctx.dao.getProducts(ids))

  val Resolver = DeferredResolver.fetchers(recipeFetcher, ingredientFetcher, productFetcher)
  val ErrorHandler = ExceptionHandler {
    case (m, AuthenticationException(message)) ⇒ HandledException(message)
    case (m, AuthorizationException(message)) ⇒ HandledException(message)
  }

  val schema = new Schema(query = QueryType, mutation = Some(MutationType))
}
