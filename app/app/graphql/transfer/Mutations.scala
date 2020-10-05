package app.graphql.transfer

import app.AppContext
import app.graphql.auth.Tags._
import app.models.ProductTransfer
import sangria.schema.{Argument, Field, IntType, ListInputType, ObjectType, fields}

object Mutations extends app.graphql.Mutations {
  val AProducts = Argument("products", ListInputType(IntType))

  // ListInputType is Seq: List needed
  implicit def seq2list[T](s: Seq[T]): List[T] = s.toList

  override val mutations = ObjectType("ProductTransferMutation", "Product Transfer Mutations",
    fields[AppContext, Unit](
      Field("makeReport", IntType,
        arguments = AProducts :: Nil,
        resolve = c => c.ctx.dao.create(ProductTransfer(products = c.arg(AProducts))),
        tags = WorkshopManagerTag :: Nil)
    ))
}
