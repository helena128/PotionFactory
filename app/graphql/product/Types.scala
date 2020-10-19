package graphql.product

import graphql.Types.IdentifiableWithIntType
import graphql.recipe.Fetchers._
import graphql.recipe.Types._
import models._
import sangria.macros.derive.{Interfaces, ObjectTypeName, ReplaceField, deriveObjectType}
import sangria.schema.{Field, ListType, StringType}
import security.AppContext

object Types {
  implicit val ProductType = deriveObjectType[AppContext, Product](
    Interfaces(IdentifiableWithIntType),
    ObjectTypeName("Product"),
    ReplaceField("tags", Field("tags", ListType(StringType),
      resolve = c => c.value.tags.asInstanceOf[List[String]])),
    ReplaceField("recipe", Field("recipe", RecipeType,
      resolve = c => recipeFetcher.defer(c.value.recipe))))
}
