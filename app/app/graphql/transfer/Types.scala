package app.graphql.transfer

import app.AppContext
import app.models.{ProductTransfer, ProductTransferStatus}
import sangria.macros.derive.{EnumTypeName, IncludeValues, Interfaces, ObjectTypeName, ReplaceField, deriveEnumType, deriveObjectType}
import sangria.schema.{Field, ListType}
import app.graphql.Types._
import app.graphql.product.Types._
import app.graphql.product.Fetchers._

object Types {
  implicit val ProductTransferStatusType =
    deriveEnumType[ProductTransferStatus.Value](EnumTypeName("ProductTransferStatus"),
      IncludeValues("Produced", "Transferred", "Stored"))
  implicit val ProductTransferType = deriveObjectType[AppContext, ProductTransfer](
    Interfaces(IdentifiableWithIntType),
    ObjectTypeName("ProductTransfer"),
    ReplaceField("products", Field("products", ListType(ProductType),
      resolve = c => productFetcher.deferSeq(c.value.products))))
}
