package app.graphql.middleware

import app.AppContext
import app.graphql.auth.Tags.RoleTag
import sangria.execution.{Middleware, MiddlewareBeforeField, MiddlewareQueryContext}
import sangria.schema.Context

object AuthMiddleware extends Middleware[AppContext] with MiddlewareBeforeField[AppContext] {
  override type QueryVal = Unit
  override type FieldVal = Unit

  override def beforeQuery(context: MiddlewareQueryContext[AppContext, _, _]) = ()

  override def afterQuery(queryVal: QueryVal, context: MiddlewareQueryContext[AppContext, _, _]) = ()

  override def beforeField(queryVal: QueryVal, mctx: MiddlewareQueryContext[AppContext, _, _], c: Context[AppContext, _]) = {
    println("Field: " + c.field)
    println("User: " + c.ctx.currentUser)

    val user = c.ctx.currentUser
    val roleTags = c.field.tags.filter(_.isInstanceOf[RoleTag])
    val passed = roleTags.isEmpty || roleTags.exists { case rt: RoleTag => rt.check(user) }

    (passed, c.ctx.currentUser.isEmpty) match {
      case (true, _) => continue
      case (false, true) => throw AuthorizationException("Permission denied. You have no rights")
      case (false, false) => throw AuthorizationException("Permission denied. Please sign in")
    }
  }

  case class AuthorizationException(message: String) extends Exception
  case class AuthenticationException(message: String) extends Exception
}
