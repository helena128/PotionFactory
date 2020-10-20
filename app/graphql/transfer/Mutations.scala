package graphql.transfer

import graphql.auth.Tags._
import models.ProductTransfer
import models.ProductTransfer.Status
import sangria.schema._
import security.AppContext

object Mutations extends graphql.Mutations {
  val AProducts = Argument("products", ListInputType(IntType))
  val AProductTransferId = Argument("productTransferId", IntType)

  // ListInputType is Seq: List needed
  implicit def seq2list[T](s: Seq[T]): List[T] = s.toList

  override val mutations = ObjectType("ProductTransferMutation", "Product Transfer Mutations",
    fields[AppContext, Unit](
      Field("makeReport", IntType,
        arguments = AProducts :: Nil,
        resolve = c =>
          c.ctx.dao.create(ProductTransfer(status = Status.Produced, products = c.arg(AProducts))),
        tags = WorkshopManagerTag :: Nil),
      Field("transferProducts", BooleanType,
        arguments = List( AProductTransferId ),
        resolve = c => c.ctx.dao.changeProductTransferStatus(c.arg(AProductTransferId), Status.Transfer),
        tags = List( WorkshopManagerTag )),
      Field("receiveProducts", BooleanType,
        arguments = List( AProductTransferId ),
        resolve = c => c.ctx.dao.changeProductTransferStatus(c.arg(AProductTransferId), Status.Stored),
        tags = List( WorkshopManagerTag )
      )
    )
  )
}
