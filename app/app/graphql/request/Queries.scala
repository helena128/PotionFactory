package app.graphql.request

import app.AppContext
import sangria.schema._
import app.graphql.Args._
import app.graphql.auth.Tags._
import Types._

object Queries extends app.graphql.Queries {
  val queries = ObjectType("IngredientRequestQuery", "Ingredient Request Queries",
    fields[AppContext, Unit](
      Field("request", IngredientRequestType,
        arguments = List(IdInt),
        resolve = c => c.ctx.dao.getIngredientRequest(c.arg(IdInt)),
        tags = List(ManagerTag, WarehouseTag)
      )
    ))
}
