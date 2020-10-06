package graphql.product

import graphql.product.Fetchers.productFetcher
import graphql.Types.IdentifiableWithIntType
import sangria.macros.derive.{EnumTypeName, IncludeValues, Interfaces, ObjectTypeName, ReplaceField, deriveEnumType, deriveObjectType}
import sangria.schema.{Field, ListType, StringType}
import graphql.recipe.Fetchers._
import graphql.recipe.Types._
import security.AppContext
import models._

object Types {
  implicit val ProductType = deriveObjectType[AppContext, Product](
    Interfaces(IdentifiableWithIntType),
    ObjectTypeName("Product"),
    ReplaceField("tags", Field("tags", ListType(StringType),
      resolve = c => c.value.tags.asInstanceOf[List[String]])),
    ReplaceField("recipe", Field("recipe", RecipeType,
      resolve = c => recipeFetcher.defer(c.value.recipe))))
}
