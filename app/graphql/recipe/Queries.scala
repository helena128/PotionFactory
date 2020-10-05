package graphql.recipe

import sangria.schema._
import Types._
import Fetchers._
import graphql.Args._
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
