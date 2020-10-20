package graphql.recipe

import graphql.auth.Tags._
import sangria.schema._
import sangria.macros.derive._
import sangria.marshalling._
import security.AppContext
import models.Recipe
import Types._

object Mutations extends graphql.Mutations {
  implicit val _request_input = new FromInput[Recipe] {
    implicit val marshaller = CoercedScalaResultMarshaller.default
    override def fromResult(node: marshaller.Node): Recipe = {
      val m = node.asInstanceOf[Map[String, Any]]
      Recipe(
        name = m("name").asInstanceOf[String],
        description = m("description").asInstanceOf[String],
        ingredients = m("ingredients").asInstanceOf[Seq[Int]].toList)
    }}

  val ARecipe = Argument("recipe",
    deriveInputObjectType[Recipe](
      InputObjectTypeName("RecipeArg"),
      ExcludeInputFields("id"),
      ReplaceInputField("ingredients", InputField("ingredients", ListInputType(IntType)))
    )
  )

  override val mutations: ObjectType[AppContext, Unit] = ObjectType("RecipeMutations", "Recipe Mutations",
    fields[AppContext, Unit](
      Field("createRecipe", RecipeType,
        arguments = List(ARecipe),
        resolve = c => c.ctx.dao.createRecipe(c.arg(ARecipe)),
        tags = List(FairyTag)
      )
    )
  )
}
