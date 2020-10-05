package graphql.recipe

import models.Recipe
import sangria.execution.deferred.Fetcher
import security.AppContext

object Fetchers extends graphql.Fetchers[Recipe, Recipe, Int] {
  val recipeFetcher: Fetcher[AppContext, Recipe, Recipe, Int] =
    Fetcher((ctx: AppContext, ids: Seq[Int]) => ctx.dao.getRecipes(ids))

  val fetchers = List(recipeFetcher)
}
