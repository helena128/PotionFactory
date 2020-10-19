package graphql.ingredient

import graphql.Types._
import graphql.ingredient.Fetchers._
import models.{Ingredient, IngredientRequest}
import sangria.macros.derive.{Interfaces, ObjectTypeName, ReplaceField, deriveObjectType}
import sangria.schema.{Field, ListType}
import security.AppContext

object Types {
  implicit val IngredientType = deriveObjectType[AppContext, Ingredient](
    Interfaces(IdentifiableWithIntType),
    ObjectTypeName("Ingredient"))

  implicit val IngredientRequestType = deriveObjectType[AppContext, IngredientRequest](
    Interfaces(IdentifiableWithIntType),
    ObjectTypeName("IngredientRequest"),
    ReplaceField("ingredients", Field("ingredients", ListType(IngredientType),
      resolve = c => ingredientFetcher.deferSeq(c.value.ingredients))))
}
