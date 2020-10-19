package graphql.order

import graphql.Args.IdInt
import graphql.auth.Tags.ClientTag
import graphql.order.Fetchers._
import graphql.order.Types._
import sangria.schema.{Field, ObjectType, fields}
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
