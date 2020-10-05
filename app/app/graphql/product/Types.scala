package app.graphql.product

import app.AppContext
import app.graphql.product.Fetchers.productFetcher
import app.graphql.Types.IdentifiableWithIntType
import app.models.{Product, ProductTransfer, ProductTransferStatus}
import sangria.macros.derive.{EnumTypeName, IncludeValues, Interfaces, ObjectTypeName, ReplaceField, deriveEnumType, deriveObjectType}
import sangria.schema.{Field, ListType, StringType}
import app.graphql.recipe.Fetchers._
import app.graphql.recipe.Types._

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
