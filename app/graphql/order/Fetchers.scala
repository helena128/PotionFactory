package graphql.order

import models.{Ingredient, Order}
import sangria.execution.deferred.Fetcher
import security.AppContext

object Fetchers extends graphql.Fetchers[Order, Order, Int] {
  val orderFetcher: Fetcher[AppContext, Order, Order, Int] =
    Fetcher((ctx: AppContext, ids: Seq[Int]) => ctx.dao.getOrders(ids))

  val fetchers = Seq(orderFetcher)
}
