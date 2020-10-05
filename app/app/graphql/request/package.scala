package app.graphql

import app.AppContext
import sangria.schema.Schema

package object request extends app.graphql.HasSchema {
  override val schema: Schema[AppContext, Unit] = Schema(query = Queries(), mutation = Mutations())
}
