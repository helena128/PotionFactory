package graphql.product

import sangria.schema.{DeferredValue, Field, ListType, ObjectType, fields}
import Fetchers._
import graphql.Args._
import graphql.product.Types._
import security.AppContext

object Queries extends graphql.Queries {
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
