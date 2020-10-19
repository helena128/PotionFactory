package graphql

import sangria.schema.Schema

package object order extends graphql.HasSchema {
  val schema = new Schema(query = Queries(), mutation = Mutations())
}
