package config

import java.time.ZonedDateTime
import java.util.UUID

import models.{IngredientRequest, ProductTransfer, _}
import slick.jdbc.{GetResult, JdbcType}
import config.PostgresProfile.api._
import slick.ast.BaseTypedType
import slick.sql.SqlProfile.ColumnOption.SqlType

import scala.util.Random

object DBSchema {
  private val rand = new Random()

  implicit class RandomExtended(r: Random) {
    def between(a: Int, b: Int): Int = a + r.nextInt(b-a)
  }

//  case class MyObject(id: Int, s: String)
//  class MyObjectTable(tag: Tag) extends Table[MyObject](tag, "MYOBJECTS") {
//    def id = column[Int]("ID", O.PrimaryKey)
//    def s = column[String]("S")
//    def * = (id, s) <> (MyObject.tupled, MyObject.unapply)
//  }

  implicit val UserRoleMapper = MappedColumnType.base[User.Role, String](_.toString, User.Role.withName)
  implicit val UserStatusMapper = MappedColumnType.base[User.Status, String](_.toString, User.Status.withName)

  class UserTable(tag: Tag) extends Table[User](tag, "USERS") {
    val id = column[String]("ID", O.PrimaryKey)
    val password = column[String]("PASSWORD")
    val name = column[String]("NAME")
    val phone = column[Option[String]]("PHONE")
    val address = column[Option[String]]("ADDRESS")
    val role = column[User.Role]("ROLE")
    val status = column[User.Status]("STATUS")
    val * = (id, password, name, phone, address, role, status) <> (
      User.hashedTupled,
      User.unapply
    )
  }
  val Users = TableQuery[UserTable]

  implicit val ConfirmationStatusMapper =
    MappedColumnType.base[AccountConfirmation.Status, String](_.toString, AccountConfirmation.Status.withName)
  class AccountConfirmationsTable(tag: Tag) extends Table[AccountConfirmation](tag, "ACCOUNT_CONFIRMATIONS") {
    val id = column[UUID]("ID", O.PrimaryKey)
    val userId = column[String]("USER_ID")
    val status = column[AccountConfirmation.Status]("STATUS")
    val activeUntil = column[ZonedDateTime]("ACTIVE_UNTIL")
    val createdAt = column[ZonedDateTime]("CREATED_AT")
    val modifiedAt = column[ZonedDateTime]("MODIFIED_AT")

    val * = (id, userId, status, activeUntil, createdAt, modifiedAt).mapTo[AccountConfirmation]

    val userFK = foreignKey("userIdFK", userId, Users)(_.id)
  }
  val AccountConfirmations = TableQuery[AccountConfirmationsTable]

  implicit val KnowledgeKindMapper = MappedColumnType.base[Knowledge.Kind, String](_.toString, Knowledge.Kind.withName)
  val KnowledgeTable = "KNOWLEDGES"
  class KnowledgeTable(tag: Tag) extends Table[Knowledge](tag, KnowledgeTable) {
    val id = column[Int]("ID", SqlType("SERIAL"), O.PrimaryKey, O.AutoInc)
    val kind = column[Knowledge.Kind]("KIND")
    val name = column[String]("NAME")
    val addedAt = column[ZonedDateTime]("ADDED_AT")
    val content = column[String]("CONTENT")
    val * = (id, kind, name, addedAt, content).mapTo[Knowledge]
  }
  val Knowledges = TableQuery[KnowledgeTable]

  class IngredientTable(tag: Tag) extends Table[Ingredient](tag, "INGREDIENTS") {
    val id = column[Int]("ID", SqlType("SERIAL"), O.PrimaryKey, O.AutoInc)
    val name = column[String]("NAME")
    val addedAt = column[ZonedDateTime]("ADDED_AT")
    val description = column[String]("DESCRIPTION")
    val count = column[Int]("COUNT")
    val * = (id, name, addedAt, description, count).mapTo[Ingredient]
  }
  val Ingredients = TableQuery[IngredientTable]

  implicit val IngredientRequestStatusMapper
  : JdbcType[IngredientRequest.Status] with BaseTypedType[IngredientRequest.Status]
  = MappedColumnType.base[IngredientRequest.Status, String](_.toString, IngredientRequest.Status.withName)

  class IngredientRequestTable(tag: Tag) extends Table[IngredientRequest](tag, "INGREDIENT_REQUESTS") {
    val id = column[Int]("ID", SqlType("SERIAL"), O.PrimaryKey, O.AutoInc)
    val status = column[IngredientRequest.Status]("STATUS")
    val ingredients = column[IngredientList]("INGREDIENTS")
    val * = (id, status, ingredients).mapTo[IngredientRequest]
  }
  val IngredientRequests = TableQuery[IngredientRequestTable]

  class RecipeTable(tag: Tag) extends Table[Recipe](tag, "RECIPES") {
    val id = column[Int]("ID", SqlType("SERIAL"), O.PrimaryKey, O.AutoInc)
    val name = column[String]("NAME")
    val description = column[String]("DESCRIPTION")
    val ingredients = column[IngredientList]("INGREDIENTS")
    val * = (id, name, description, ingredients).mapTo[Recipe]
  }
  val Recipes = TableQuery[RecipeTable]

  class ProductTable(tag: Tag) extends Table[Product](tag, "PRODUCTS") {
    val id = column[Int]("ID", SqlType("SERIAL"), O.PrimaryKey, O.AutoInc)
    val name = column[String]("NAME")
    val tags = column[ProductTags]("TAGS")
    val description = column[String]("DESCRIPTION")
    val recipe = column[Int]("RECIPE_ID")
    val count = column[Int]("COUNT")
    val basePrice = column[Double]("BASE_PRICE")
    val * = (id, name, tags, description, recipe, count, basePrice).mapTo[Product]

    val recipeFK = foreignKey("recipeIdFK", recipe, Recipes)(_.id)
  }
  val Products = TableQuery[ProductTable]

  class OrderTable(tag: Tag) extends Table[Order](tag, "ORDERS") {
    val id = column[Int]("ID", SqlType("SERIAL"), O.PrimaryKey, O.AutoInc)
    val content = column[Int]("CONTENT")
    val count = column[Int]("COUNT")
    val orderedBy = column[String]("ORDERED_BY")
    val * = (id, content, count, orderedBy).mapTo[Order]

    val contentFK = foreignKey("contentFK", content, Products)(_.id)
    val orderedByFK = foreignKey("orderedByFK", orderedBy, Users)(_.id)
  }
  val Orders = TableQuery[OrderTable]

  implicit val ProductTransferStatusMapper:
    JdbcType[ProductTransfer.Status] with BaseTypedType[ProductTransfer.Status]
  = MappedColumnType.base[ProductTransfer.Status, String](_.toString, ProductTransfer.Status.withName)

  class ProductTransferTable(tag: Tag) extends Table[ProductTransfer](tag, "PRODUCT_TRANSFERS") {
    val id = column[Int]("ID", SqlType("SERIAL"), O.PrimaryKey, O.AutoInc)
    val status = column[ProductTransfer.Status]("STATUS")
    val products = column[ProductList]("CONTENTS")
    def * = (id, status, products).mapTo[ProductTransfer]
  }
  val ProductTransfers = TableQuery[ProductTransferTable]

  class UserSessionTable(tag: Tag) extends Table[(String, String)](tag, "SESSIONS") {
    val id = column[String]("ID", O.PrimaryKey)
    val user_id = column[String]("USER_ID")

    val * = (id, user_id)

    val userFK = foreignKey("userFK", user_id, Users)(_.id)
  }
  val UserSessions = TableQuery[UserSessionTable]

  /*
   * Populate database with stub values
   */

  import Knowledge.Kind._
  private implicit def tupToUser(t: (String, String, String, String, String, User.Role, User.Status)): User =
    User(t._1, t._2, t._3, Option(t._4), Option(t._5), t._6, t._7, isHashedPassword = false)

  private val plantain = Ingredient(0, "Plantain", ZonedDateTime.now().minusDays(10), "It heals", rand.between(10, 100))
  private val oliveOil =
    Ingredient(1, "Olive Oil", ZonedDateTime.now().minusDays(300), "Makes your skin smooth", rand.between(10, 100))
  private val parrotsHorn = Ingredient(2, "Parrot's Horn", ZonedDateTime.now(), "What?", rand.between(0, 10))

  private val plantainPotion = Recipe(0, "Plantain potion", "Extract of plantain", List.fill(5)(plantain.id))
  private val superOliveOil = Recipe(1, "Super Olive oil", "Olive oil enhanced with plantain", List(oliveOil.id, plantain.id))

  private val plantainPotionProd = Product(0, "Plantain potion",
    List("health", "heals"),
    "Extract of plantain", plantainPotion.id,
    rand.between(1000, 100000), rand.between(1, 5).toDouble)
  private val superOliveOilProd = Product(1, "Super Olive oil",
    List("smooth skin", "health"),
    "Olive oil enhanced with plantain", superOliveOil.id,
    rand.between(10, 100), rand.between(5, 10).toDouble)

  val schema = Seq(
    Users, Knowledges, Ingredients, IngredientRequests, Recipes, Products, Orders, ProductTransfers,
    UserSessions,
    AccountConfirmations)
    .map(_.schema)
    .reduce(_ ++ _)

  val setup = DBIO.seq(
    schema.createIfNotExists,

    {
      import User.Role._
      import User.Status._

      Users insertOrUpdateAll
        Seq[User](
          ("god@potions.ml", "qwerty", "Admin", "555-0000", "Transcendent", Admin, Active),
          ("fairy@potions.ml", "qwerty", "Fairy Godmother", "555-0001", "Potions Factory", Fairy, Active),
          ("warehouse@potions.ml", "qwerty", "Warehouse Manager", "555-1111", "Potions Factory", WarehouseManager, Active),
          ("workshop@potions.ml", "qwerty", "Workshop Manager", "555-2222", "Potions Factory", WorkshopManager, Active),
          ("johndoe@example.com", "qwerty", "John Doe", "555-5555", "Bottom of the ocean", Client, Active),
          ("mitch@potions.ml", "qwerty", "Dollar Mitch", "555-8888", "Potions Factory", WorkshopWorker, Active),
          ("joe@potions.ml", "qwerty", "Sixteen Joe", "555-9999", "Potions Factory", WorkshopWorker, Active)
        )
    },

    Knowledges insertOrUpdateAll Seq(
      (0, Gossip, "Somebody told me", ZonedDateTime.now().minusDays(7),
        "I've heard that if you mix cucumber with milk it can help your digestion"),
      (1, Book, "THE BOOK OF GENESIS", ZonedDateTime.now().minusYears(930),
        "[2:16] And the LORD God commanded the man, \"You may freely eat of every tree of the garden;\n" +
        "[2:17] but of the tree of the knowledge of good and evil you shall not eat, " +
        "for in the day that you eat of it you shall die.\""),
      (2, Myth, "The Myth of Ambrosia", ZonedDateTime.now(),
        "In Greek mythology, ambrosia was considered the food or drink of the Olympian gods, " +
        "and it was thought to bring long life and immortality to anyone who consumed it."),
      (3, Fable, "Alice’s Adventures in Wonderland", ZonedDateTime.parse("1865-11-26T00:00:00Z"),
        """
        Soon her eye fell on a little glass box that was lying under the table:
        she opened it, and found in it a very small cake,
        on which the words “EAT ME” were beautifully marked in currants.
        “Well, I’ll eat it,” said Alice, “and if it makes me grow larger,
        I can reach the key; and if it makes me grow smaller, I can creep under the door;
        so either way I’ll get into the garden, and I don’t care which happens!
        """.trim().replaceAll("\\s+", " "))
    ).map(Knowledge.tupled),

    Ingredients insertOrUpdateAll Seq(plantain, oliveOil, parrotsHorn),

    {
      import IngredientRequest.Status._

      IngredientRequests insertOrUpdateAll Seq(
        (0, Open, List.fill(1)(plantain.id) ++ List.fill(2)(oliveOil.id) ++ List.fill(3)(parrotsHorn.id)),
        (1, Open, List.fill(10)(plantain.id) ++ List.fill(4)(oliveOil.id) ++ List.fill(2)(parrotsHorn.id)),
        (2, Transfer, List.fill(2)(plantain.id) ++ List.fill(10)(oliveOil.id) ++ List.fill(34)(parrotsHorn.id)),
        (3, Transfer, List.fill(7)(plantain.id) ++ List.fill(133)(oliveOil.id) ++ List.fill(28)(parrotsHorn.id)),
        (4, Received, List.fill(12)(plantain.id) ++ List.fill(84)(oliveOil.id) ++ List.fill(12)(parrotsHorn.id)),
        (5, Received, List.fill(53)(plantain.id) ++ List.fill(16)(oliveOil.id) ++ List.fill(13)(parrotsHorn.id))
      ).map(IngredientRequest.tupled)
    },

    Recipes insertOrUpdateAll Seq(plantainPotion, superOliveOil),

    Products insertOrUpdateAll Seq(plantainPotionProd, superOliveOilProd),

    Orders insertOrUpdateAll Seq((0, plantainPotionProd.id, 3, "johndoe@example.com")).map(Order.tupled),

    ProductTransfers insertOrUpdateAll Seq(
      (0, ProductTransfer.Status.Produced, List.fill(100)(plantainPotionProd.id)),
      (1, ProductTransfer.Status.Transfer, List.fill(5)(plantainPotionProd.id) ++ List.fill(10)(superOliveOilProd.id)),
      (2, ProductTransfer.Status.Stored, List.fill(50)(superOliveOilProd.id)),
    ).map(ProductTransfer.tupled),
  )

  implicit val getSearchKnowledgeResult: GetResult[Seq[String]] =
    GetResult[Seq[String]](_.nextObject().asInstanceOf[Seq[String]])
}
