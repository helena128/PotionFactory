package app.graphql.user

import app.AppContext
import app.graphql.Args._
import Types._
import sangria.schema.{Field, ObjectType, OptionType, fields}

object Queries extends app.graphql.Queries {
  val queries = ObjectType("UserQuery", "User Queries",
    fields[AppContext, Unit](
      Field("user", OptionType(UserType),
        arguments = IdStr :: Nil,
        resolve = c => c.ctx.dao.getUser(c.arg(IdStr)),
        tags = Nil
      )))
}
