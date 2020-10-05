package app.graphql.order

import app.AppContext
import app.graphql.Args.IdInt
import app.graphql.ingredient.Fetchers.ingredientFetcher
import app.graphql.ingredient.Types.IngredientType
import sangria.schema.{Field, ListType, ObjectType, fields}
import Fetchers._
import app.graphql.auth.Tags.ClientTag
import Types._

object Queries extends app.graphql.Queries {
  val queries = ObjectType("OrderQuery", "Order Queries",
    fields[AppContext, Unit](
      Field("order", OrderType,
        arguments = List(IdInt),
        resolve = c => orderFetcher.defer(c.arg(IdInt)),
        tags = List(ClientTag)
      )))
}
