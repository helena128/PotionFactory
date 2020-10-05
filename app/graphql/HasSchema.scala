package graphql

import sangria.schema.Schema
import security.AppContext

trait HasSchema {
  val schema: Schema[AppContext, Unit];
  def apply(): Schema[AppContext, Unit] = schema
}
