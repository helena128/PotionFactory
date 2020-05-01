package app

import slick.jdbc.H2Profile.api._
import app.DBSchema._
import app.schema.Models._

import scala.concurrent.Future

case class DAO(db: Database) {
  def getUser(id: String): Future[User] = db run Users.filter(_.id === id).result.head
  def searchKnowledge(s: String, limit: Int): Future[Seq[Knowledge]] =
    db run Knowledges.filter(_.content like s).take(limit).result
  def getRecipe(id: Int): Future[Recipe]= db run Recipes.filter(_.id === id).result.head
  def getRecipes(ids: Seq[Int]): Future[Seq[Recipe]] = db run Recipes.filter(_.id inSet ids).result
  def getIngredients(ids: Seq[Int]): Future[Seq[Ingredient]] = db run Ingredients.filter(_.id inSet ids).result
  def getAllIngredients: Future[Seq[Ingredient]] = db run Ingredients.result
  def getProducts(ids: Seq[Int]): Future[Seq[Product]] = db run Products.filter(_.id inSet ids).result
  def getAllProducts: Future[Seq[Product]] = db run Products.result

  def create(u: User): Future[User] = db run (Users.returning(Users) += u)
  def create(o: Order): Future[Order] = db run (Orders.returning(Orders) += o)
  def create(req: IngredientRequest): Future[IngredientRequest] =
    db run (IngredientRequests.returning(IngredientRequests) += req)
  def create(t: ProductTransfer): Future[ProductTransfer] =
    db run (ProductTransfers.returning(ProductTransfers) += t)
}
