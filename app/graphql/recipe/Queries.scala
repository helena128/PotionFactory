package graphql.recipe

import graphql.Args._
import graphql.auth.Tags._
import graphql.recipe.Fetchers._
import graphql.recipe.Types._
import sangria.schema._
import security.AppContext

object Queries extends graphql.Queries {
  private val tags = List(FairyTag, WorkshopTag)
  val queries = ObjectType("RecipeQuery", "Recipe Query",
    fields[AppContext, Unit](
      Field("recipe", RecipeType,
        arguments = List(IdInt),
        resolve = c => DeferredValue(recipeFetcher.defer(c.arg(IdInt))),
        tags = tags
      ),
      Field("allRecipes", ListType(RecipeType),
        resolve = c => c.ctx.dao.getAllRecipes(),
        tags = tags)
    ))
}
