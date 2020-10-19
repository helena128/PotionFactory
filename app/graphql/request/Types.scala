package graphql.request

import graphql.Types.IdentifiableWithIntType
import graphql.ingredient.Fetchers._
import graphql.ingredient.Types._
import models.IngredientRequest
import sangria.macros.derive._
import sangria.schema._
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
