package app.graphql.transfer

import sangria.schema._
import Types._
import app.graphql.Args._
import app.AppContext
import app.graphql.auth.Tags._

object Queries extends app.graphql.Queries {
  val queries = ObjectType("ProductTransferQuery", "Product Transfer Queries",
    fields[AppContext, Unit](
      Field("report", ProductTransferType,
        arguments = List(IdInt),
        resolve = c => c.ctx.dao.getProductTransfer(c.arg(IdInt)),
        tags = List(ManagerTag)
      )))
}
