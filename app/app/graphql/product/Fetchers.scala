package app.graphql.product

import app.AppContext
import app.models.Product
import sangria.execution.deferred.Fetcher

object Fetchers extends app.graphql.Fetchers[Product, Product, Int] {
  val productFetcher: Fetcher[AppContext, Product, Product, Int] =
    Fetcher((ctx: AppContext, ids: Seq[Int]) => ctx.dao.getProducts(ids))

  val fetchers = List(productFetcher)
}
