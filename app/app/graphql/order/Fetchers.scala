package app.graphql.order

import app.AppContext
import app.models.{Ingredient, Order}
import sangria.execution.deferred.Fetcher

object Fetchers extends app.graphql.Fetchers[Order, Order, Int] {
  val orderFetcher: Fetcher[AppContext, Order, Order, Int] =
    Fetcher((ctx: AppContext, ids: Seq[Int]) => ctx.dao.getOrders(ids))

  val fetchers = Seq(orderFetcher)
}
