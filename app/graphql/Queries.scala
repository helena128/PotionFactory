package graphql

import sangria.schema.ObjectType
import security.AppContext

trait Queries {
  val queries: ObjectType[AppContext, Unit]
  def apply(): ObjectType[AppContext, Unit] = queries
}