package graphql

import sangria.schema.Schema
import security.AppContext

package object transfer extends graphql.HasSchema {
  override val schema: Schema[AppContext, Unit] = Schema(query = Queries(), mutation = Mutations())
}
