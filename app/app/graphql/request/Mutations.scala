package app.graphql.request

import app.AppContext
import app.graphql.auth.Tags._
import app.models.IngredientRequest
import sangria.macros.derive.{ExcludeInputFields, InputObjectTypeName, ReplaceInputField, deriveInputObjectType}
import sangria.marshalling.{CoercedScalaResultMarshaller, FromInput}
import sangria.schema.{Argument, Field, InputField, IntType, ListInputType, ObjectType, fields}

object Mutations extends app.graphql.Mutations {
  implicit val _request_input = new FromInput[IngredientRequest] {
    implicit val marshaller = CoercedScalaResultMarshaller.default
    override def fromResult(node: marshaller.Node): IngredientRequest = {
      val m = node.asInstanceOf[Map[String, Any]]
      IngredientRequest(ingredients = m("ingredients").asInstanceOf[Seq[Int]].toList)
    }}

  val ARequest = Argument("request",
    deriveInputObjectType[IngredientRequest](InputObjectTypeName("RequestArg"),
      ExcludeInputFields("id"),
      ReplaceInputField("ingredients", InputField("ingredients", ListInputType(IntType)))))

  override val mutations = ObjectType("IngredientRequestMutation", "Ingredient Request Mutations",
    fields[AppContext, Unit](
      Field("requestIngredient", IntType,
        arguments = ARequest :: Nil,
        resolve = c => c.ctx.dao.create(c.arg(ARequest)),
        tags = WorkshopManagerTag :: Nil)))
}
