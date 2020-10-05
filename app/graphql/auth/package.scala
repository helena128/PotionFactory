package graphql

import sangria.schema.Schema

package object auth extends graphql.HasSchema {
  val schema = new Schema(query = Queries(), mutation = Mutations())
}
