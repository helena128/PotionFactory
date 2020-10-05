package app.graphql.ingredient

import app.AppContext
import app.models.Ingredient
import sangria.execution.deferred.Fetcher

object Fetchers extends app.graphql.Fetchers[Ingredient, Ingredient, Int] {
  val ingredientFetcher: Fetcher[AppContext, Ingredient, Ingredient, Int] =
    Fetcher((ctx: AppContext, ids: Seq[Int]) => ctx.dao.getIngredients(ids))

  val fetchers = List(ingredientFetcher)
}
