package graphql.order

import graphql.Args.IdInt
import graphql.auth.Tags._
import graphql.order.Fetchers._
import graphql.order.Types._
import sangria.schema._
import security.AppContext

object Queries extends graphql.Queries {
  val queries = ObjectType("OrderQuery", "Order Queries",
    fields[AppContext, Unit](
      Field("order", OrderType,
        arguments = List(IdInt),
        resolve = c => orderFetcher.defer(c.arg(IdInt)),
        tags = List(ClientTag)
      ),
      Field("orders", ListType(OrderType),
        resolve = c => c.ctx.dao.getUserOrders(c.ctx.currentUser.get.id),
        tags = List(ClientTag)
      )
    )
  )
}
