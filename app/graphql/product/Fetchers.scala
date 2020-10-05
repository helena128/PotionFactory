package graphql.product

import models.Product
import sangria.execution.deferred.Fetcher
import security.AppContext

object Fetchers extends graphql.Fetchers[Product, Product, Int] {
  val productFetcher: Fetcher[AppContext, Product, Product, Int] =
    Fetcher((ctx: AppContext, ids: Seq[Int]) => ctx.dao.getProducts(ids))

  val fetchers = List(productFetcher)
}
