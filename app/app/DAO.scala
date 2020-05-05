package app

import java.time.ZonedDateTime

import slick.jdbc.H2Profile.api._
import app.DBSchema._
import app.schema.Models._
import slick.jdbc.{GetResult, PositionedResult}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

case class DAO(db: Database) {
  def getUser(id: String): Future[User] = db run Users.filter(_.id === id).result.head

  implicit val getKnowledge = GetResult(
    r => Knowledge(
      r.nextInt(), KnowledgeKind(r.nextString()),
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

  def getRecipe(id: Int): Future[Recipe]= db run Recipes.filter(_.id === id).result.head
  def getRecipes(ids: Seq[Int]): Future[Seq[Recipe]] = db run Recipes.filter(_.id inSet ids).result
  def getIngredients(ids: Seq[Int]): Future[Seq[Ingredient]] = db run Ingredients.filter(_.id inSet ids).result
  def getAllIngredients: Future[Seq[Ingredient]] = db run Ingredients.result
  def getProducts(ids: Seq[Int]): Future[Seq[Product]] = db run Products.filter(_.id inSet ids).result
  def getAllProducts: Future[Seq[Product]] = db run Products.result

  def create(u: User): Future[String] = db run (Users.returning(Users.map(_.id)) += u)
  def create(o: Order): Future[Int] = db run (Orders.returning(Orders.map(_.id)) += o)
  def create(req: IngredientRequest): Future[Int] =
    db run (IngredientRequests.returning(IngredientRequests.map(_.id)) += req)
  def create(t: ProductTransfer): Future[Int] =
    db run (ProductTransfers.returning(ProductTransfers.map(_.id)) += t)
}
