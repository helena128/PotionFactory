package app.graphql.ingredient

import app.AppContext
import app.graphql.auth.Tags._
import sangria.schema.{Field, ListType, ObjectType, fields}
import app.graphql.Types._
import Types._
import Fetchers._
import app.graphql.Args._

object Queries extends app.graphql.Queries {
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
