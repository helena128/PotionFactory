package graphql.recipe

import graphql.Args._
import graphql.recipe.Fetchers._
import graphql.recipe.Types._
import sangria.schema._
import security.AppContext

object Queries extends graphql.Queries {
  val queries = ObjectType("RecipeQuery", "Recipe Query",
    fields[AppContext, Unit](
      Field("recipe", RecipeType,
        arguments = List(IdInt),
        resolve = c => DeferredValue(recipeFetcher.defer(c.arg(IdInt)))
      )
    ))
}
