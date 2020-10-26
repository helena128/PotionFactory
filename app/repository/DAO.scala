package repository

import java.time.ZonedDateTime
import java.util.UUID

import config.DBSchema._
import config.PostgresProfile
import models._
import slick.dbio.{DBIOAction, NoStream}
import slick.jdbc.GetResult
import config.PostgresProfile.api._
import org.postgresql.util.PSQLException

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent._
import scala.util.matching.Regex.Groups

case class DAO(db: Database) {
  def handleConstraintViolation[R]: PartialFunction[Throwable, Future[R]] = {
    case ex: PSQLException =>
      val msg = ex.getMessage()
      val regex =
        "^ERROR: duplicate key value violates unique constraint \"(?<constraint>\\w+)\"\\s*Detail: Key \\(\"(?<column>\\w+)\"\\)=\\((?<value>.+)\\) already exists.$".r

    regex.findFirstMatchIn(msg) match {
        case Some(Groups(constraint, column, value)) =>
          throw ConstraintViolationException(constraint, column, value)
        case None =>
          throw ex
      }
    case e =>
      throw e
  }

  def run[R](action: DBIOAction[R, NoStream, Nothing]): Future[R] = dbrun(action)
  private def dbrun[R](action: DBIOAction[R, NoStream, Nothing]): Future[R] =
    db.run(action).recoverWith(handleConstraintViolation)
  private implicit class DBIOToFuture[R](action: DBIOAction[R, NoStream, Nothing]) {
    def run(): Future[R] = dbrun(action).recoverWith(handleConstraintViolation)
  }
  private implicit def runDBIO[R](action: DBIOAction[R, NoStream, Nothing]): Future[R] = dbrun(action)

  def getUser(id: String): Future[User] = Users.filter(_.id === id).result.head
  def authenticate(id: String, password: String): Future[Option[User]] = {
    Users
      .filter(_.id === id)
      .result.headOption
      .run()
      .map(_.filter(_.hasPassword(password)))
  }

  def getAllUsers(): Future[Seq[User]] = Users.result

  implicit val getKnowledge: GetResult[Knowledge] = GetResult(
    r => Knowledge(
      r.nextInt(), Knowledge.Kind(r.nextString()),
      r.nextString(), ZonedDateTime.parse(r.nextString(), PostgresProfile.api.date2TzDateTimeFormatter), r.nextString()))

  def searchKnowledge(s: String, limit: Int, lookaround: Int): Future[Seq[Knowledge]] =
      sql"""
            select "ID", "KIND", "NAME", "ADDED_AT",
            substring("CONTENT" from greatest(0, (position($s in "CONTENT")-$lookaround)) for ${2*lookaround})
            from postgres."public"."KNOWLEDGES"
            where position($s in "CONTENT") != 0
            limit $limit
            """
    .as[Knowledge]
  def getKnowledge(id: Int): Future[Knowledge] = Knowledges.filter(_.id === id).result.head

  def getRecipe(id: Int): Future[Recipe] = Recipes.filter(_.id === id).result.head
  def getRecipes(ids: Seq[Int]): Future[Seq[Recipe]] = Recipes.filter(_.id inSet ids).result
  def getAllRecipes(): Future[Seq[Recipe]] = Recipes.result
  def createRecipe(recipe: Recipe): Future[Recipe] =
    (Recipes.returning(Recipes.map(_.id)) += recipe).run().map(id => recipe.copy(id = id))

  def getIngredients(ids: Seq[Int]): Future[Seq[Ingredient]] = Ingredients.filter(_.id inSet ids).result
  def getAllIngredients: Future[Seq[Ingredient]] = Ingredients.result

  def getProducts(ids: Seq[Int]): Future[Seq[Product]] = Products.filter(_.id inSet ids).result
  def getAllProducts: Future[Seq[Product]] = Products.result

  def getOrders(ids: Seq[Int]): Future[Seq[Order]] = Orders.filter(_.id inSet ids).result
  def getUserOrders(id: String): Future[Seq[Order]] = Orders.filter(_.orderedBy === id).result

  def getProductTransfer(id: Int): Future[ProductTransfer] = ProductTransfers.filter(_.id === id).result.head
  def changeProductTransferStatus(id: Int, status: ProductTransfer.Status): Future[Boolean] =
    ProductTransfers.filter(_.id === id).map(_.status).update(status).run().map(_ > 0)

  def getIngredientRequest(id: Int): Future[IngredientRequest] = IngredientRequests.filter(_.id === id).result.head
  def changeIngredientRequestStatus(id: Int, status: IngredientRequest.Status): Future[Boolean] =
    IngredientRequests.filter(_.id === id).map(_.status).update(status).run().map(_ > 0)

  def create(u: User): Future[User] = {
    Users.returning(Users.map(identity)) += u
  }

  def create(c: AccountConfirmation): Future[AccountConfirmation] =
    AccountConfirmations.returning(AccountConfirmations.map(identity)) += c
  def create(o: Order): Future[Int] =
    Orders.returning(Orders.map(_.id)) += o
  def create(req: IngredientRequest): Future[Int] =
    IngredientRequests.returning(IngredientRequests.map(_.id)) += req
  def create(t: ProductTransfer): Future[Int] =
    ProductTransfers.returning(ProductTransfers.map(_.id)) += t

  def update(user: User): Future[Option[User]] =
    Users
      .filter(_.id === user.id)
      .update(user)
      .map(c => if (c > 0) Some(user) else None)

  def deactivateUser(userId: String): Future[Boolean] =
    Users
      .filter(_.id === userId)
      .map(_.status)
      .update(User.Status.Deactivated)
      .run()
      .map(_ > 0)

  def storeSession(id: String, user_id: String): Future[Boolean] = {
    val session = (id, user_id)
    UserSessions.insertOrUpdate(session).run().map(_ > 0)
  }

  def getSessionUser(id: String): Future[Option[User]] =
    UserSessions
    .filter(_.id === id)
    .join(Users)
    .on(_.user_id === _.id)
    .map(_._2)
    .result.headOption
    .run()

  def isSessionActive(id: String): Future[Boolean] = UserSessions.filter(_.id === id).exists.result
  def listUserSessions(): Future[Seq[(String, String)]] = UserSessions.map(r => (r.id, r.user_id)).result
  def deleteSession(id: String): Future[Boolean] = UserSessions.filter(_.id === id).delete.run().map(_ > 0)

  def confirm(id: UUID): Future[Option[User]] =
    AccountConfirmations
      .filter(c => c.id === id && c.activeUntil <= ZonedDateTime.now())
      .map(_.status)
      .update(AccountConfirmation.Status.Fulfilled)
      .map(_ == 0)
      .flatMap {
        case false => DBIOAction.from(Future(Option.empty[User]))
        case true =>
          DBIOAction.from(
            db.run(
              (AccountConfirmations.join(Users).on(_.userId === _.id)
                .filter(_._1.id === id)
                .map(_._2)
                .result
                .headOption)))
      }
      .transactionally
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
