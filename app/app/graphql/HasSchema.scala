package app.graphql

import app.AppContext
import sangria.schema.Schema

trait HasSchema {
  val schema: Schema[AppContext, Unit];
  def apply(): Schema[AppContext, Unit] = schema
}
