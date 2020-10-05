package graphql.ingredient

import models.Ingredient
import sangria.execution.deferred.Fetcher
import security.AppContext

object Fetchers extends graphql.Fetchers[Ingredient, Ingredient, Int] {
  val ingredientFetcher: Fetcher[AppContext, Ingredient, Ingredient, Int] =
    Fetcher((ctx: AppContext, ids: Seq[Int]) => ctx.dao.getIngredients(ids))

  val fetchers = List(ingredientFetcher)
}
