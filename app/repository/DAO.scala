package repository

import java.time.ZonedDateTime

import config.DBSchema._
import models._
import slick.dbio.{DBIOAction, NoStream}
import slick.jdbc.GetResult
import slick.jdbc.H2Profile.api._
import utils.Serializer._

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent._

case class DAO(db: Database) {
  def run[R](a: DBIOAction[R, NoStream, Nothing]): Future[R] = db.run(a)

  def getUser(id: String): Future[User] = db run Users.filter(_.id === id).result.head
  def authenticate(id: String, password: String): Future[Option[User]] =
    (db run Users.filter(_.id === id).result.headOption)
    .map(_.filter(_.hasPassword(password)))
  def getAllUsers(): Future[Seq[User]] = db run Users.result

  implicit val getKnowledge = GetResult(
    r => Knowledge(
      r.nextInt(), Knowledge.Kind(r.nextString()),
      r.nextString(), ZonedDateTime.parse(r.nextString()), r.nextString()))

  def searchKnowledge(s: String, limit: Int, lookaround: Int): Future[Seq[Knowledge]] =
    (db run
        sql"""
              select id, kind, name, added_at,
              substring(content from greatest(0, (position($s in content)-$lookaround)) for ${2*lookaround})
              from knowledges
              where position($s in content) != 0
              limit $limit
              """
      .as[Knowledge])
  def getKnowledge(id: Int): Future[Knowledge] = db run Knowledges.filter(_.id === id).result.head

  def getRecipe(id: Int): Future[Recipe] = db run Recipes.filter(_.id === id).result.head
  def getRecipes(ids: Seq[Int]): Future[Seq[Recipe]] = db run Recipes.filter(_.id inSet ids).result

  def getIngredients(ids: Seq[Int]): Future[Seq[Ingredient]] = db run Ingredients.filter(_.id inSet ids).result
  def getAllIngredients: Future[Seq[Ingredient]] = db run Ingredients.result

  def getProducts(ids: Seq[Int]): Future[Seq[Product]] = db run Products.filter(_.id inSet ids).result
  def getAllProducts: Future[Seq[Product]] = db run Products.result

  def getOrders(ids: Seq[Int]): Future[Seq[Order]] = db run Orders.filter(_.id inSet ids).result

  def getProductTransfer(id: Int): Future[ProductTransfer] = db run ProductTransfers.filter(_.id === id).result.head

  def getIngredientRequest(id: Int): Future[IngredientRequest] = db run IngredientRequests.filter(_.id === id).result.head

  def create(u: User): Future[String] = db run (Users.returning(Users.map(_.id)) += u)
  def create(o: Order): Future[Int] = db run (Orders.returning(Orders.map(_.id)) += o)
  def create(req: IngredientRequest): Future[Int] =
    db run (IngredientRequests.returning(IngredientRequests.map(_.id)) += req)
  def create(t: ProductTransfer): Future[Int] =
    db run (ProductTransfers.returning(ProductTransfers.map(_.id)) += t)

  def storeSession[T <: Serializable](id: String, a: T): Future[Boolean] = {
    val session = (id, a.serialize)
    (db run (Sessions += session)).map(_ > 0)
  }

  def getSession[T <: Serializable](id: String): Future[Option[T]] =
    (db run
      (Sessions
        .filter(_.id === id)
        .map(_.content)
        .result
        .headOption)
      .map(_.map(_.deserialize[T])))
  def isSessionActive(id: String): Future[Boolean] = db run Sessions.filter(_.id === id).exists.result
  def listSessions(): Future[Seq[(String, Array[Byte])]] = db run Sessions.map(r => (r.id, r.content)).result
  def deleteSession(id: String): Future[Boolean] = (db run Sessions.filter(_.id === id).delete).map(_ > 0)
}

//object DAO {
//  private[repository] def apply(): DAO = {
//    val db = Database.forConfig("h2mem")
//
//    db.run(sql"""SHOW TABLES""".as[String])
//      .andThen({case t =>
//        if (t.get.nonEmpty) Await.result(db.run(schema.dropIfExists), 10 seconds)
//        Await.result(db.run(setup), 10 seconds)
//      })
//
//    new DAO(db)
//  }
//}
