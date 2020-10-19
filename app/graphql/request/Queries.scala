package graphql.request

import graphql.Args._
import graphql.auth.Tags._
import graphql.request.Types._
import sangria.schema._
import security.AppContext

object Queries extends graphql.Queries {
  val queries = ObjectType("IngredientRequestQuery", "Ingredient Request Queries",
    fields[AppContext, Unit](
      Field("request", IngredientRequestType,
        arguments = List(IdInt),
        resolve = c => c.ctx.dao.getIngredientRequest(c.arg(IdInt)),
        tags = List(ManagerTag, WarehouseTag)
      )
    ))
}
