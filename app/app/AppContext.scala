package app

import app.schema.Models.User

case class AppContext(dao: DAO, currentUser: Option[User] = None){
//  def login(email: String, password: String): User = {
//    val userOpt = Await.result(dao.authenticate(email, password), Duration.Inf)
//    userOpt.getOrElse(
//      throw AuthenticationException("email or password are incorrect!")
//    )
//  }
//
//  def ensureAuthenticated() =
//    if(currentUser.isEmpty)
//      throw AuthorisationException("You do not have permission. Please sign in.")
}