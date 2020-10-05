package app.graphql.auth

import app.AppContext
import app.graphql.auth.Tags._
import sangria.schema._
import app.graphql.user.Types._

object Queries extends app.graphql.Queries {
  val queries: ObjectType[AppContext, Unit] = ObjectType("AuthQuery", "Schema Queries",
    fields[AppContext, Unit](
      Field("loggedIn", BooleanType,
        resolve = c => c.ctx.currentUser.nonEmpty),

      Field("currentUser", OptionType(UserType),
        resolve = c => c.ctx.currentUser,
        tags = AuthenticatedTag :: Nil
      )))
}
