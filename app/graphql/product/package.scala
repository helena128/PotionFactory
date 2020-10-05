package graphql

import sangria.schema.Schema

package object product extends graphql.HasSchema {
  val schema = new Schema(query = Queries())
}
