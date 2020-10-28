package graphql.request

import graphql.auth.Tags._
import models.IngredientRequest
import sangria.macros.derive.{ExcludeInputFields, InputObjectTypeName, ReplaceInputField, deriveInputObjectType}
import sangria.marshalling.{CoercedScalaResultMarshaller, FromInput}
import sangria.schema._
import security.AppContext

object Mutations extends graphql.Mutations {
  implicit val _request_input = new FromInput[IngredientRequest] {
    implicit val marshaller = CoercedScalaResultMarshaller.default
    override def fromResult(node: marshaller.Node): IngredientRequest = {
      val m = node.asInstanceOf[Map[String, Any]]
      IngredientRequest(ingredients = m("ingredients").asInstanceOf[Seq[Int]].toList)
    }}

  val ARequest = Argument("request",
    deriveInputObjectType[IngredientRequest](InputObjectTypeName("RequestArg"),
      ExcludeInputFields("id", "status", "createdAt"),
      ReplaceInputField("ingredients", InputField("ingredients", ListInputType(IntType)))))

  val ARequestId = Argument("requestId", IntType)

  override val mutations = ObjectType("IngredientRequestMutation", "Ingredient Request Mutations",
    fields[AppContext, Unit](
      Field("requestIngredient", IntType,
        arguments = ARequest :: Nil,
        resolve = c => c.ctx.dao.create(c.arg(ARequest)),
        tags = WorkshopManagerTag :: Nil),
      Field("transferIngredients", BooleanType,
        arguments = List(ARequestId),
        resolve = c => c.ctx.dao.changeIngredientRequestStatus(c.arg(ARequestId), IngredientRequest.Status.Transfer),
        tags = List(WarehouseManagerTag)),
      Field("receiveIngredients", BooleanType,
        arguments = List(ARequestId),
        resolve = c => c.ctx.dao.changeIngredientRequestStatus(c.arg(ARequestId), IngredientRequest.Status.Received),
        tags = List(WorkshopManagerTag))
    ))
}
