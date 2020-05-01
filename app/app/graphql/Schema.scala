package app.graphql

import java.time.ZonedDateTime

import app.{AppContext, DBSchema}
import sangria.schema._
import sangria.execution.deferred._
import sangria.macros.derive._
import app.schema.Models._
import org.joda.time.DateTime
import sangria.ast.StringValue
import sangria.validation.Violation

import scala.util.Try

object Schema {
  def apply(): Schema[AppContext, Unit] = schema

  /*
   * Common
   */
  case object DateTimeCoerceViolation extends Violation {
    override def errorMessage: String = "Error parsing DateTime"
  }

  implicit val GraphQLDateTime = ScalarType[ZonedDateTime]("ZonedDateTime",
    coerceOutput = (dt, _) => dt.toString,
    coerceInput = {
      case StringValue(s, _, _, _, _) =>
        Try(ZonedDateTime.parse(s)).toEither.fold(_ => Left(DateTimeCoerceViolation), Right(_))
      case _ => Left(DateTimeCoerceViolation)
    },
    coerceUserInput = {
      case s: String =>
        Try(ZonedDateTime.parse(s)).toEither.fold(_ => Left(DateTimeCoerceViolation), Right(_))
      case _ => Left(DateTimeCoerceViolation)
    }
  )

  implicit val IdentifiableWithStringType = InterfaceType(
    "IdentifiableWithString",
    "Entity that can be identified with String",
    fields[AppContext, Identifiable[String]](Field("id", StringType, resolve = _.value.id)))

  implicit val IdentifiableWithIntType = InterfaceType(
    "IdentifiableWithInt",
    "Entity that can be identified with Integer",
    fields[AppContext, Identifiable[Int]](Field("id", IntType, resolve = _.value.id)))

  /*
   * User
   */

  //  private implicit val UserRoleType = EnumType()
  //  private implicit val UserRoleType = ObjectType("UserRole", "User's Role in the System",
  //    fields[UserRepo, Unit](
  //      Field("")
  //    ))
  implicit val UserRoleType = deriveEnumType[UserRole.Value](EnumTypeName("UserRole"),
    IncludeValues("Admin", "Fairy", "Client", "WorkshopManager", "WarehouseManager"))

  implicit val UserType = deriveObjectType[AppContext, User](
    Interfaces(IdentifiableWithStringType),
    ObjectTypeName("User"),
    ObjectTypeDescription("User account and info"))

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

  val recipeFetcher: Fetcher[AppContext, Recipe, Recipe, Int] =
    Fetcher((ctx: AppContext, ids: Seq[Int]) => ctx.dao.getRecipes(ids))
  val ingredientFetcher: Fetcher[AppContext, Ingredient, Ingredient, Int] =
    Fetcher((ctx: AppContext, ids: Seq[Int]) => ctx.dao.getIngredients(ids))
  val productFetcher: Fetcher[AppContext, Product, Product, Int] =
    Fetcher((ctx: AppContext, ids: Seq[Int]) => ctx.dao.getProducts(ids))


  val IdStr = Argument("id", StringType)
  val IdInt = Argument("id", IntType)
  val SearchString = Argument("string", StringType)
  val SearchLimit = Argument("limit", IntType)

  //  def userById(id: String) = Users.filter(_.id == id).take(1)
//

  /*
* UseCases
** 1. Order Creation (3.1.4)
** 2. Search Literature (3.1.7)
** 3. Ingredients Request (3.1.10)
** 4. "Potion produced" 3.1.13
 */

  val QueryType = ObjectType("Query", "Schema Queries",
    fields[AppContext, Unit](
      Field("user", OptionType(UserType), arguments = IdStr :: Nil, resolve = c => c.ctx.dao.getUser(c.arg(IdStr))),
      Field("searchKnowledge", ListType(KnowledgeType),
        arguments = SearchString :: SearchLimit :: Nil,
        resolve = c => c.ctx.dao.searchKnowledge(c.arg(SearchString), c.arg(SearchLimit))),
      Field("ingredient", IngredientType,
        arguments = IdInt :: Nil,
        resolve = c => ingredientFetcher.defer(c.arg(IdInt))),
      Field("allIngredients", ListType(IngredientType),
        resolve = c => c.ctx.dao.getAllIngredients),
      Field("product", ProductType,
        arguments = IdInt :: Nil,
        resolve = c => productFetcher.defer(c.arg(IdInt))),
      Field("allProducts", ListType(ProductType),
        resolve = c => c.ctx.dao.getAllProducts),
    ))

  val Username = Argument("username", StringType)
  val Password = Argument("password", StringType)
  val Name = Argument("name", StringType)
  val Email = Argument("email", StringType)
  val Phone = Argument("phone", OptionInputType(StringType))
  val Address = Argument("address", OptionInputType(StringType))

  val AProduct = Argument("product", IntType)
  val AOrderedBy = Argument("orderedBy", StringType)

  val IngredientList = Argument("ingredients", ListInputType(IntType))

  val AProducts = Argument("products", ListInputType(IntType))

  // ListInputType is Seq: List needed
  private implicit def seq2list[T](s: Seq[T]) = s.toList

  val MutationType = ObjectType("Mutation", "Schema Mutations",
    fields[AppContext, Unit](
      Field("createOrder", OrderType,
        arguments = AProduct :: AOrderedBy :: Nil,
        resolve = c => c.ctx.dao.create(Order(
          product = c.arg(AProduct),
          orderedBy = c.arg(AOrderedBy)))),
//      Field("createUser", UserType,
//        arguments = Username :: Password ::
//          Name :: Email :: Phone :: Address :: Nil,
//        resolve = c => c.ctx.dao.create(User(
//          c.arg(Username), c.arg(Password),
//          c.arg(Name), c.arg(Email), c.arg(Phone), c.arg(Address)))),
      Field("requestIngredient", IngredientRequestType,
        arguments = IngredientList :: Nil,
        resolve =
          c => c.ctx.dao.create(
            IngredientRequest(ingredients = c.arg(IngredientList)))),
      Field("makeReport", ProductTransferType,
        arguments = AProducts :: Nil,
        resolve = c => c.ctx.dao.create(
          ProductTransfer(products = c.arg(AProducts))))),
  )


  val Resolver = DeferredResolver.fetchers(recipeFetcher, ingredientFetcher, productFetcher)

  val schema = new Schema(query = QueryType, mutation = Some(MutationType))
}
