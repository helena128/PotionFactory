package app.graphql.transfer

import app.AppContext
import sangria.schema.Schema

package object transfer extends app.graphql.HasSchema {
  override val schema: Schema[AppContext, Unit] = Schema(query = Queries(), mutation = Mutations())
}
