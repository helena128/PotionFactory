package graphql.order

import graphql.auth.Tags.ClientTag
import models.Order
import sangria.macros.derive._
import sangria.marshalling._
import sangria.schema._
import security.AppContext

object Mutations extends graphql.Mutations {
  implicit val _order_input = new FromInput[Order] {
    implicit val marshaller = CoercedScalaResultMarshaller.default
    override def fromResult(node: marshaller.Node): Order = {
      val m = node.asInstanceOf[Map[String, Any]]
      Order(
        product = m("product".asInstanceOf[String]).asInstanceOf[Int],
        count = m("count").asInstanceOf[Int],
        orderedBy = null)
    }}

  val AOrder = Argument("order",
    deriveInputObjectType[Order](InputObjectTypeName("OrderArg"),
      ExcludeInputFields("id", "orderedBy")
    ))

  override val mutations = ObjectType("OrderMutation", "Order Mutations",
    fields[AppContext, Unit](
      Field("createOrder", IntType,
        arguments = AOrder :: Nil,
        resolve = c => {
          val order = c.arg(AOrder)
          c.ctx.dao.create(order.copy(orderedBy = c.ctx.currentUser.get.id))
        },
        tags = ClientTag :: Nil)))
}
