package graphql.recipe

import graphql.Types._
import graphql.ingredient.Fetchers._
import graphql.ingredient.Types._
import models.Recipe
import sangria.macros.derive.{Interfaces, ObjectTypeName, ReplaceField, deriveObjectType}
import sangria.schema.{Field, ListType}
import security.AppContext

object Types {
  implicit val RecipeType = deriveObjectType[AppContext, Recipe](
    Interfaces(IdentifiableWithIntType),
    ObjectTypeName("Recipe"),
    ReplaceField("ingredients", Field("ingredients", ListType(IngredientType),
      resolve = c => ingredientFetcher.deferSeq(c.value.ingredients))))
}
