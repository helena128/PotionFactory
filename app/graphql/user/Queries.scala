package graphql.user

import graphql.Args._
import graphql.auth.Tags.AdminTag
import graphql.user.Types._
import sangria.schema.{Field, ListType, ObjectType, OptionType, fields}
import security.AppContext

object Queries extends graphql.Queries {
  val queries = ObjectType("UserQuery", "User Queries",
    fields[AppContext, Unit](
      Field("user", OptionType(UserType),
        arguments = IdStr :: Nil,
        resolve = c => c.ctx.dao.getUser(c.arg(IdStr)),
        tags = List(AdminTag)
      ),
      Field("allUsers", ListType(UserType),
        resolve = c => c.ctx.dao.getAllUsers(),
        tags = List(AdminTag)
      )
    ))
}
