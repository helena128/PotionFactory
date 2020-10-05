package app.graphql.order

import app.AppContext
import app.models.Order
import sangria.macros.derive.{Interfaces, ObjectTypeName, ReplaceField, deriveObjectType}
import app.graphql.Types._
import app.graphql.product.Types._
import app.graphql.product.Fetchers._
import sangria.schema.Field


object Types {
  implicit val OrderType = deriveObjectType[AppContext, Order](
    Interfaces(IdentifiableWithIntType),
    ObjectTypeName("Order"),
    ReplaceField("product", Field("product", ProductType,
      resolve = c => productFetcher.defer(c.value.product))))
}
