package graphql.ingredient

import graphql.Args._
import graphql.auth.Tags._
import graphql.ingredient.Fetchers._
import graphql.ingredient.Types._
import sangria.schema.{Field, ListType, ObjectType, fields}
import security.AppContext

object Queries extends graphql.Queries {
  val queries = ObjectType("IngredientQuery", "Ingredient Queries",
    fields[AppContext, Unit](
      Field("ingredient", IngredientType,
        arguments = IdInt :: Nil,
        resolve = c => ingredientFetcher.defer(c.arg(IdInt)),
        tags = WorkerTag :: Nil),
      Field("allIngredients", ListType(IngredientType),
        resolve = c => c.ctx.dao.getAllIngredients,
        tags = WorkerTag :: Nil)))
}
