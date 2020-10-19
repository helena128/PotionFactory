package graphql.transfer

import graphql.Args._
import graphql.auth.Tags._
import graphql.transfer.Types._
import sangria.schema._
import security.AppContext

object Queries extends graphql.Queries {
  val queries = ObjectType("ProductTransferQuery", "Product Transfer Queries",
    fields[AppContext, Unit](
      Field("report", ProductTransferType,
        arguments = List(IdInt),
        resolve = c => c.ctx.dao.getProductTransfer(c.arg(IdInt)),
        tags = List(ManagerTag)
      )))
}
