package graphql.transfer

import graphql.Types._
import graphql.product.Fetchers._
import graphql.product.Types._
import models.ProductTransfer
import models.ProductTransfer._
import sangria.macros.derive._
import sangria.schema.{Field, ListType}
import security.AppContext

object Types {
  implicit val ProductTransferStatusType =
    deriveEnumType[ProductTransfer.Status.Value](EnumTypeName("ProductTransferStatus"),
      IncludeValues("Produced", "Transfer", "Stored"))
  implicit val ProductTransferType = deriveObjectType[AppContext, ProductTransfer](
    Interfaces(IdentifiableWithIntType),
    ObjectTypeName("ProductTransfer"),
    ReplaceField("products", Field("products", ListType(ProductType),
      resolve = c => productFetcher.deferSeq(c.value.products))))
}
