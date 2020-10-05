package graphql.auth

import graphql.auth.Tags._
import sangria.schema._
import graphql.user.Types._
import security.AppContext

object Queries extends graphql.Queries {
  val queries: ObjectType[AppContext, Unit] = ObjectType("AuthQuery", "Schema Queries",
    fields[AppContext, Unit](
      Field("loggedIn", BooleanType,
        resolve = c => c.ctx.currentUser.nonEmpty),

      Field("currentUser", OptionType(UserType),
        resolve = c => c.ctx.currentUser,
        tags = AuthenticatedTag :: Nil
      )))
}
