package graphql.order

import models.Order
import sangria.macros.derive.{Interfaces, ObjectTypeName, ReplaceField, deriveObjectType}
import graphql.Types._
import graphql.product.Types._
import graphql.product.Fetchers._
import sangria.schema.Field
import security.AppContext


object Types {
  implicit val OrderType = deriveObjectType[AppContext, Order](
    Interfaces(IdentifiableWithIntType),
    ObjectTypeName("Order"),
    ReplaceField("product", Field("product", ProductType,
      resolve = c => productFetcher.defer(c.value.product))))
}
