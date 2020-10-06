package graphql

import sangria.schema.ObjectType
import security.AppContext

trait Mutations {
  val mutations: ObjectType[AppContext, Unit] = null;
  def apply(): Option[ObjectType[AppContext, Unit]] = Option(mutations)
}
