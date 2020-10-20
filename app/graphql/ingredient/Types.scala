package graphql.ingredient

import graphql.Types._
import graphql.ingredient.Fetchers._
import models.{Ingredient, IngredientRequest}
import IngredientRequest.Status
import sangria.macros.derive._
import sangria.schema._
import security.AppContext

object Types {
  implicit val IngredientType = deriveObjectType[AppContext, Ingredient](
    Interfaces(IdentifiableWithIntType),
    ObjectTypeName("Ingredient"))

  implicit val IngredientRequestStatusType = deriveEnumType[IngredientRequest.Status.Value](
    EnumTypeName("IngredientRequestStatus"),
    IncludeValues("Open", "Transfer", "Received"))

  implicit val IngredientRequestType = deriveObjectType[AppContext, IngredientRequest](
    Interfaces(IdentifiableWithIntType),
    ObjectTypeName("IngredientRequest"),
    ReplaceField("ingredients", Field("ingredients", ListType(IngredientType),
      resolve = c => ingredientFetcher.deferSeq(c.value.ingredients))))
}
