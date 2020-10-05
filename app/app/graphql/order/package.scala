package app.graphql

import sangria.schema.{ObjectType, Schema}

package object order extends app.graphql.HasSchema {
  val schema = new Schema(query = Queries(), mutation = Mutations())
}
