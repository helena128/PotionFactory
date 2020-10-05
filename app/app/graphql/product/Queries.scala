package app.graphql.product

import app.AppContext
import sangria.schema.{DeferredValue, Field, ListType, ObjectType, fields}
import Fetchers._
import app.graphql.Args._
import app.graphql.product.Types._

object Queries extends app.graphql.Queries {
  val queries =
    ObjectType("ProductQuery", "Product Queries",
      fields[AppContext, Unit](
        Field("product", ProductType,
          arguments = IdInt :: Nil,
          resolve = c => DeferredValue(productFetcher.defer(c.arg(IdInt)))),
        Field("allProducts", ListType(ProductType),
            resolve = c => c.ctx.dao.getAllProducts))
    )
}
