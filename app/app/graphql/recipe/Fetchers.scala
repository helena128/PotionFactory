package app.graphql.recipe

import app.AppContext
import app.models.Recipe
import sangria.execution.deferred.Fetcher

object Fetchers extends app.graphql.Fetchers[Recipe, Recipe, Int] {
  val recipeFetcher: Fetcher[AppContext, Recipe, Recipe, Int] =
    Fetcher((ctx: AppContext, ids: Seq[Int]) => ctx.dao.getRecipes(ids))

  val fetchers = List(recipeFetcher)
}
