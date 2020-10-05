package graphql.product

import graphql.product.Fetchers.productFetcher
import graphql.Types.IdentifiableWithIntType
import models.{Product, ProductTransfer, ProductTransferStatus}
import sangria.macros.derive.{EnumTypeName, IncludeValues, Interfaces, ObjectTypeName, ReplaceField, deriveEnumType, deriveObjectType}
import sangria.schema.{Field, ListType, StringType}
import graphql.recipe.Fetchers._
import graphql.recipe.Types._
import security.AppContext

object Types {
  implicit val ProductType = deriveObjectType[AppContext, Product](
    Interfaces(IdentifiableWithIntType),
    ObjectTypeName("Product"),
    ReplaceField("tags", Field("tags", ListType(StringType),
      resolve = c => c.value.tags.asInstanceOf[List[String]])),
    ReplaceField("recipe", Field("recipe", RecipeType,
      resolve = c => recipeFetcher.defer(c.value.recipe))))

  implicit val ProductTransferStatusType =
    deriveEnumType[ProductTransferStatus.Value](EnumTypeName("ProductTransferStatus"),
      IncludeValues("Produced", "Transferred", "Stored"))
  implicit val ProductTransferType = deriveObjectType[AppContext, ProductTransfer](
    Interfaces(IdentifiableWithIntType),
    ObjectTypeName("ProductTransfer"),
    ReplaceField("products", Field("products", ListType(ProductType),
      resolve = c => productFetcher.deferSeq(c.value.products))))
}
