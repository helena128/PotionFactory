package graphql

import sangria.schema.Schema

package object ingredient extends graphql.HasSchema {
  val schema = new Schema(query = Queries())
}
