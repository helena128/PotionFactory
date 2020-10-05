package app.graphql

import sangria.schema.Schema

package object auth extends app.graphql.HasSchema {
  val schema = new Schema(query = Queries(), mutation = Mutations())
}
