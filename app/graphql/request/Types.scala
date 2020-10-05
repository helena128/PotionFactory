package graphql.request

import graphql.Types.IdentifiableWithIntType
import graphql.product.Fetchers.productFetcher
import graphql.product.Types.ProductType
import models.{IngredientRequest, Order}
import sangria.macros.derive._
import sangria.schema._
import graphql.ingredient.Types._
import graphql.ingredient.Fetchers._
import security.AppContext

object Types {
  implicit val IngredientRequestType = deriveObjectType[AppContext, IngredientRequest](
    Interfaces(IdentifiableWithIntType),
    ObjectTypeName("IngredientRequest"),
    ReplaceField("ingredients", Field("ingredients", ListType(IngredientType),
      resolve = c => ingredientFetcher.deferSeq(c.value.ingredients)
    ))
  )
}
