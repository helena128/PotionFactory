package app

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, ObjectInputStream, ObjectOutputStream, Serializable}
import java.time.ZonedDateTime

import scala.util.Random
import app.schema.Models._
import com.fasterxml.jackson.databind.ObjectMapper
import slick.jdbc.GetResult
import slick.sql.SqlProfile.{ColumnOption => CO}

import scala.concurrent.duration._
import scala.language.postfixOps
import scala.concurrent.Await
import scala.reflect.ClassTag

//import slick.model._
//import slick.lifted._
import slick.jdbc.H2Profile.api._
import scala.concurrent.ExecutionContext.Implicits.global

object DBSchema {
  private def serialize(t: Serializable): Array[Byte] = {
    val stream: ByteArrayOutputStream = new ByteArrayOutputStream()
    val oos = new ObjectOutputStream(stream)
    oos.writeObject(t)
    oos.close()
    stream.toByteArray
  }

  private def deserialize[T <: Serializable](b: Array[Byte]): T = {
    val ois = new ObjectInputStream(new ByteArrayInputStream(b))
    val value = ois.readObject()
    ois.close()
    value.asInstanceOf[T]
  }

  private def objectMapper[A <: Serializable](implicit tag: ClassTag[A]) =
    MappedColumnType.base[A, Array[Byte]](serialize, deserialize[A])

  //  private val json = new ObjectMapper()

  //  private def json_mapper[A <: Map[B, Any], B <: Any, C <: AnyVal](mapper: B => C) =
  //    MappedColumnType.base[A, String](
  //      m => json.writeValueAsString(m map {case (k, v) => k -> v}),
  //      s => json.readValue[A](s, classOf[A]))

  //  private implicit val IngredientListMapper =
  //    MappedColumnType.base[Ingredients, Any](
  //      m => json.writeValueAsString(m map {case (k, v) => k.id -> v}),
  //      s => json.readValue[Map[Int, Int]](s, classOf[Map[Int, Int]]).map({case (id, count) => })
  //    )
  //    json_mapper[Ingredients, Ingredient, Int](_.id)
  //  private implicit val IngredientListMapper =
  //    MappedColumnType.base[Ingredients, Any](
  //      (SerializationUtils.serialize),
  //      (_.asInstanceOf[Ingredients])
  //    )
  //

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

  implicit val UserRoleMapper = MappedColumnType.base[UserRole, String](_.toString, UserRole.withName)
  class UserTable(tag: Tag) extends Table[User](tag, "USERS") {
    val id = column[String]("ID", O.PrimaryKey)
    val password = column[String]("PASSWORD")
    val name = column[String]("NAME")
    val email = column[String]("EMAIL")
    val phone = column[Option[String]]("PHONE")
    val address = column[Option[String]]("ADDRESS")
    val role = column[UserRole]("ROLE")
    val * = (id, password, name, email, phone, address, role).mapTo[User]
  }
  val Users = TableQuery[UserTable]

  implicit val KnowledgeKindMapper = MappedColumnType.base[KnowledgeKind, String](_.toString, KnowledgeKind.withName)
  class KnowledgeTable(tag: Tag) extends Table[Knowledge](tag, "KNOWLEDGES") {
    val id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
    val kind = column[KnowledgeKind]("KIND")
    val name = column[String]("NAME")
    val addedAt = column[ZonedDateTime]("ADDED_AT")
    val content = column[String]("CONTENT")
    val * = (id, kind, name, addedAt, content).mapTo[Knowledge]
  }
  val Knowledges = TableQuery[KnowledgeTable]

  class IngredientTable(tag: Tag) extends Table[Ingredient](tag, "INGREDIENTS") {
    val id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
    val name = column[String]("NAME")
    val addedAt = column[ZonedDateTime]("ADDED_AT")
    val description = column[String]("DESCRIPTION")
    val count = column[Int]("COUNT")
    val * = (id, name, addedAt, description, count).mapTo[Ingredient]
  }
  val Ingredients = TableQuery[IngredientTable]

  class IngredientRequestTable(tag: Tag) extends Table[IngredientRequest](tag, "INGREDIENT_REQUESTS") {
    private implicit val ingredientListMapper = objectMapper[IngredientList]

    val id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
    val ingredients = column[IngredientList]("INGREDIENTS")
    val * = (id, ingredients).mapTo[IngredientRequest]
  }
  val IngredientRequests = TableQuery[IngredientRequestTable]

  class RecipeTable(tag: Tag) extends Table[Recipe](tag, "RECIPES") {
    private implicit val ingredientListMapper = objectMapper[IngredientList]

    val id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
    val name = column[String]("NAME")
    val description = column[String]("DESCRIPTION")
    val ingredients = column[IngredientList]("INGREDIENTS")
    val * = (id, name, description, ingredients).mapTo[Recipe]
  }
  val Recipes = TableQuery[RecipeTable]

  class ProductTable(tag: Tag) extends Table[Product](tag, "PRODUCTS") {
    private implicit val tagsMapper = objectMapper[ProductTags]

    val id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
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

  class OrderTable(tag: Tag) extends Table[Order](tag, "ORDER") {
    val id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
    val content = column[Int]("CONTENT")
    val count = column[Int]("COUNT")
    val orderedBy = column[String]("ORDERED_BY")
    val * = (id, content, count, orderedBy).mapTo[Order]

    val contentFK = foreignKey("contentFK", content, Products)(_.id)
    val orderedByFK = foreignKey("orderedByFK", orderedBy, Users)(_.id)
  }
  val Orders = TableQuery[OrderTable]



  private implicit val ProductTransferStatusMapper =
    MappedColumnType.base[ProductTransferStatus, String](_.toString, ProductTransferStatus.withName)

  class ProductTransferTable(tag: Tag) extends Table[ProductTransfer](tag, "PRODUCT_TRANSFERS") {
    private implicit val productListMapper = objectMapper[ProductList]

    val id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
    val status = column[ProductTransferStatus]("STATUS")
    val products = column[ProductList]("CONTENTS")
    def * = (id, status, products).mapTo[ProductTransfer]
  }
  val ProductTransfers = TableQuery[ProductTransferTable]

  /*
   * Populate database with stub values
   */

  import UserRole._
  import KnowledgeKind._
  private implicit def tupToUser(t: (String, String, String, String, String, String, UserRole)): User =
    User(t._1, t._2, t._3, t._4, Option(t._5), Option(t._6), t._7)

  private val plantain = Ingredient(0, "Plantain", ZonedDateTime.now().minusDays(10), "It heals", rand.between(10, 100))
  private val oliveOil =
    Ingredient(1, "Olive Oil", ZonedDateTime.now().minusDays(300), "Makes your skin smooth", rand.between(10, 100))
  private val parrotsHorn = Ingredient(2, "Parrot's Horn", ZonedDateTime.now(), "What?", rand.between(0, 10))

  private val plantainPotion = Recipe(0, "Plantain potion", "Extract of plantain", List.fill(5)(plantain.id))
  private val superOliveOil = Recipe(1, "Super Olive oil", "Olive oil enhanced with plantain", List(oliveOil.id, plantain.id))

  private val plantainPotionProd = Product(0, "Plantain potion",
    List("health", "heals"),
    "Extract of plantain", plantainPotion.id,
    rand.between(1000, 100000), rand.between(1, 5))
  private val superOliveOilProd = Product(1, "Super Olive oil",
    List("smooth skin", "health"),
    "Olive oil enhanced with plantain", superOliveOil.id,
    rand.between(10, 100), rand.between(5, 10))

  val fullSchema = Seq(Users, Knowledges, Ingredients, IngredientRequests, Recipes, Products, Orders, ProductTransfers)
    .map(_.schema)
    .reduce(_ ++ _)

  val databaseSetup = DBIO.seq(
    fullSchema.createIfNotExists,

    Users forceInsertAll
      Seq[User](
        ("admin", "qwerty", "Admin", "good@potions.factory", "555-0000", "Transcendent", Admin),
        ("fairy", "qwerty", "Fairy Godmother", "fairy@potions.factory", "555-0001", "Potions Factory", Fairy),
        ("waremgr", "qwerty", "Warehouse Manager", "warehouse@potions.factory", "555-1111", "Potions Factory", WarehouseManager),
        ("workmgr", "qwerty", "Workshop Manager", "workshop@potions.factory", "555-2222", "Potions Factory", WorkshopManager),
        ("client", "qwerty", "John Doe", "johndoe@example.com", "555-5555", "Bottom of the ocean", Client)
      ),

    Knowledges forceInsertAll Seq(
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

    Ingredients forceInsertAll Seq(plantain, oliveOil, parrotsHorn),

    IngredientRequests forceInsertAll Seq(
      (0, List.fill(10)(plantain.id) ++ List.fill(20)(oliveOil.id) ++ List.fill(50)(parrotsHorn.id))
    ).map(IngredientRequest.tupled),

    Recipes forceInsertAll Seq(plantainPotion, superOliveOil),

    Products forceInsertAll Seq(plantainPotionProd, superOliveOilProd),

    Orders forceInsertAll Seq((0, plantainPotionProd.id, 3, "client")).map(Order.tupled),

    ProductTransfers forceInsertAll Seq(
      (0, ProductTransferStatus.Transferred, List.fill(5)(plantainPotionProd.id) ++ List.fill(10)(superOliveOilProd.id))
    ).map(ProductTransfer.tupled),
  )


  implicit val getSearchKnowledgeResult =
    GetResult[Seq[String]](_.nextObject().asInstanceOf[Seq[String]])

  def createDatabase: DAO = {
    val db = Database.forConfig("h2mem")

//    Await.result(db run sql"""SHOW TABLES""".as[String], 5 seconds).foreach(println)
    db.run(sql"""SHOW TABLES""".as[String])
      .andThen({case t =>
        if (!t.get.isEmpty) fullSchema.dropIfExists
        Await.result(db.run(databaseSetup), 10 second)
      })

    new DAO(db)
  }
}
