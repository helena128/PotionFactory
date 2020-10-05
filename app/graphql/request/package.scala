package graphql

import sangria.schema.Schema
import security.AppContext

package object request extends graphql.HasSchema {
  override val schema: Schema[AppContext, Unit] = Schema(query = Queries(), mutation = Mutations())
}
