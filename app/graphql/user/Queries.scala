package graphql.user

import graphql.Args._
import graphql.user.Types._
import sangria.schema.{Field, ObjectType, OptionType, fields}
import security.AppContext

object Queries extends graphql.Queries {
  val queries = ObjectType("UserQuery", "User Queries",
    fields[AppContext, Unit](
      Field("user", OptionType(UserType),
        arguments = IdStr :: Nil,
        resolve = c => c.ctx.dao.getUser(c.arg(IdStr)),
        tags = Nil
      )))
}
