package app

import java.util.UUID

import app.graphql.AuthMiddleware.AuthenticationException
import Models.User

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

//  def isAuthenticated() = currentUser.nonEmpty
}
object AppContext {
  def apply(sessionId: String, dao: DAO): AppContext = {
//    val results = Await.result(dao.listSessions, Duration.Inf)
//    println(results)
//    println(results.size)

    val userOpt = Await.result(dao.getSession[User](sessionId), Duration.Inf)

//    println("userOpt: " + userOpt)

    new AppContext(sessionId, dao, userOpt)
  }
}