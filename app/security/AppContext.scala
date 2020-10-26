package security

import email.MailService
import models.User
import repository.DAO

import scala.concurrent.Await
import scala.concurrent.duration.Duration

case class AppContext(sessionId: String,
                      dao: DAO, mailer: MailService,
                      currentUser: Option[User] = None){
  def login(creds: User.Credentials): Option[User] = {
    val user = Await.result(dao.authenticate(creds.id, creds.password), Duration.Inf)

    user.foreach(user =>
      Await.result(dao.storeSession(sessionId, user.id), Duration.Inf))

    user
  }

  def logout() = Await.result(dao.deleteSession(sessionId), Duration.Inf)
}
object AppContext {
  def apply(sessionId: String, dao: DAO, mailer: MailService): AppContext = {
    val userOpt = Await.result(dao.getSessionUser(sessionId), Duration.Inf)
    new AppContext(sessionId, dao, mailer, userOpt)
  }
}