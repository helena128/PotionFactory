package app.schema

import java.io.Serializable
import java.time.ZonedDateTime

import org.mindrot.jbcrypt.BCrypt
import sangria.execution.deferred.HasId

object Models {
  trait Identifiable[T] {def id: T}

  object Identifiable {
    implicit def hasId[S, T <: Identifiable[S]]: HasId[T, S] = HasId(_.id)
  }


  object UserRole extends Enumeration {
    def apply(s: String): UserRole = withName(s)

    import scala.language.implicitConversions
    implicit def fromId(id: Int): UserRole = apply(id);
    implicit def fromString(s: String): UserRole = apply(s)

    val Admin = Value(0, "admin")
    val Fairy = Value(1, "fairy")
    val Client = Value(2, "client")
    val WorkshopManager = Value(3, "workshop-manager")
    val WarehouseManager = Value(4, "warehouse-manager")
  }
  type UserRole = UserRole.Value

  case class User(id: String,
                  password: String,
                  name: String,
                  email: String,
                  phone: Option[String],
                  address: Option[String],
                  role: UserRole = UserRole.Client
                 ) extends Identifiable[String] {
    def apply(id: String, password: String,
              name: String, email: String, phone: Option[String], address: Option[String],
              role: UserRole) =
      new User(id, User.hashpw(password), name, email, phone, address, role)
  }

  object User extends {
    private val salt = BCrypt.gensalt(12)
    def hashpw(s: String): String = BCrypt.hashpw(s, salt)
    val tupled = (User.apply _).tupled
  }


  object KnowledgeKind extends Enumeration {
    protected case class Val(override val id: Int, name: String)
      extends super.Val(id)
        with Identifiable[Int]

    def apply(s: String): KnowledgeKind = withName(s)

    implicit def fromId(id: Int): KnowledgeKind = apply(id);
    implicit def fromString(s: String): KnowledgeKind = apply(s)

    val Gossip = Val(0, "gossip")
    val Book = Val(1, "book")
    val Myth = Val(2, "myth")
    val Fable = Val(3, "fable")
  }
  type KnowledgeKind = KnowledgeKind.Value

  case class Knowledge(id: Int, kind: KnowledgeKind, name: String, addedAt: ZonedDateTime, content: String)
    extends Identifiable[Int]

  case class Ingredient(id: Int, name: String, addedAt: ZonedDateTime, description: String, count: Int)
    extends Identifiable[Int]

//  type Ingredients = Map[Ingredient, Int]
  type IngredientList = List[Int] with Serializable
//  case class Ingredients() extends Map[Ingredient, Int] with Serializable {
//    def +[IntD >: Int](p: (Ingredient, IntD)) = super.+(p)
//  }

  case class IngredientRequest(id: Int = -1, ingredients: IngredientList)
    extends Identifiable[Int]

  case class Recipe(id: Int = -1, name: String, description: String, ingredients: IngredientList)
    extends Identifiable[Int]

  type ProductTag = String with Serializable
  type ProductTags = List[ProductTag] with Serializable
  case class Product(id: Int = -1, name: String, tags: ProductTags, description: String, recipe: Int, count: Int, basePrice: Double)
    extends Identifiable[Int]
  type ProductList = List[Int] with Serializable

  case class Order(id: Int = -1, product: Int, count: Int, orderedBy: String)
    extends Identifiable[Int]

  object ProductTransferStatus extends Enumeration {
    protected final case class Val(override val id: Int, name: String)
      extends super.Val(id)
        with Identifiable[Int]

    val Produced = Val(0, "produced")
    val Transferred = Val(1, "transferred")
    val Stored = Val(2, "stored")
  }
  type ProductTransferStatus = ProductTransferStatus.Value

  // TODO: Seq is ok?
  case class ProductTransfer(id: Int = -1,
                             status: ProductTransferStatus = ProductTransferStatus.Produced,
                             products: ProductList)
    extends Identifiable[Int]
}
