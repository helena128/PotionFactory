package graphql.transfer

import models.ProductTransfer
import models.ProductTransfer.Status
import sangria.macros.derive.{EnumTypeName, IncludeValues, Interfaces, ObjectTypeName, ReplaceField, deriveEnumType, deriveObjectType}
import sangria.schema.{Field, ListType}
import graphql.Types._
import graphql.product.Types._
import graphql.product.Fetchers._
import security.AppContext

object Types {
  implicit val ProductTransferStatusType =
    deriveEnumType[ProductTransfer.Status.Value](EnumTypeName("ProductTransferStatus"),
      IncludeValues("Produced", "Transferred", "Stored"))
  implicit val ProductTransferType = deriveObjectType[AppContext, ProductTransfer](
    Interfaces(IdentifiableWithIntType),
    ObjectTypeName("ProductTransfer"),
    ReplaceField("products", Field("products", ListType(ProductType),
      resolve = c => productFetcher.deferSeq(c.value.products))))
}
