package security

import models.User
import repository.DAO

import scala.concurrent.Await
import scala.concurrent.duration.Duration

case class AppContext(sessionId: String, dao: DAO, currentUser: Option[User] = None){
  def login(creds: User.Credentials): Option[User] = {
    val user = Await.result(dao.authenticate(creds.id, creds.password), Duration.Inf)

    user.foreach(user =>
      Await.result(dao.storeSession(sessionId, user), Duration.Inf))

    user
  }

  def logout() = Await.result(dao.deleteSession(sessionId), Duration.Inf)
}
object AppContext {
  def apply(sessionId: String, dao: DAO): AppContext = {
    val userOpt = Await.result(dao.getSession[User](sessionId), Duration.Inf)
    new AppContext(sessionId, dao, userOpt)
  }
}