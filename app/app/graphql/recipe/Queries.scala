package app.graphql.recipe

import app.AppContext
import sangria.schema._
import Types._
import Fetchers._
import app.graphql.Args._

object Queries extends app.graphql.Queries {
  val queries = ObjectType("RecipeQuery", "Recipe Query",
    fields[AppContext, Unit](
      Field("recipe", RecipeType,
        arguments = List(IdInt),
        resolve = c => DeferredValue(recipeFetcher.defer(c.arg(IdInt)))
      )
    ))
}
