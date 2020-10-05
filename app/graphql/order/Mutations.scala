package graphql.order

import graphql.auth.Tags.ClientTag
import models.Order
import sangria.macros.derive.{ExcludeInputFields, InputObjectTypeName, deriveInputObjectType}
import sangria.marshalling.{CoercedScalaResultMarshaller, FromInput}
import sangria.schema.{Argument, Field, IntType, ObjectType, fields}
import Types._
import security.AppContext

object Mutations extends graphql.Mutations {
  implicit val _order_input = new FromInput[Order] {
    implicit val marshaller = CoercedScalaResultMarshaller.default
    override def fromResult(node: marshaller.Node): Order = {
      val m = node.asInstanceOf[Map[String, Any]]
      Order(
        product = m("product".asInstanceOf[String]).asInstanceOf[Int],
        count = m("count").asInstanceOf[Int],
        orderedBy = m("orderedBy").asInstanceOf[String])
    }}

  val AOrder = Argument("order",
    deriveInputObjectType[Order](InputObjectTypeName("OrderArg"),
      ExcludeInputFields("id")))

  override val mutations = ObjectType("OrderMutation", "Order Mutations",
    fields[AppContext, Unit](
      Field("createOrder", IntType,
        arguments = AOrder :: Nil,
        resolve = c => c.ctx.dao.create(c.arg(AOrder)),
        tags = ClientTag :: Nil)))
}
