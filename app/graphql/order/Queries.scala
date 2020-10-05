package graphql.order

import graphql.Args.IdInt
import graphql.ingredient.Fetchers.ingredientFetcher
import graphql.ingredient.Types.IngredientType
import sangria.schema.{Field, ListType, ObjectType, fields}
import Fetchers._
import graphql.auth.Tags.ClientTag
import Types._
import security.AppContext

object Queries extends graphql.Queries {
  val queries = ObjectType("OrderQuery", "Order Queries",
    fields[AppContext, Unit](
      Field("order", OrderType,
        arguments = List(IdInt),
        resolve = c => orderFetcher.defer(c.arg(IdInt)),
        tags = List(ClientTag)
      )))
}
