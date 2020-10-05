package app.graphql.request

import app.AppContext
import app.graphql.Types.IdentifiableWithIntType
import app.graphql.product.Fetchers.productFetcher
import app.graphql.product.Types.ProductType
import app.models.{IngredientRequest, Order}
import sangria.macros.derive._
import sangria.schema._
import app.graphql.ingredient.Types._
import app.graphql.ingredient.Fetchers._

object Types {
  implicit val IngredientRequestType = deriveObjectType[AppContext, IngredientRequest](
    Interfaces(IdentifiableWithIntType),
    ObjectTypeName("IngredientRequest"),
    ReplaceField("ingredients", Field("ingredients", ListType(IngredientType),
      resolve = c => ingredientFetcher.deferSeq(c.value.ingredients)
    ))
  )
}
