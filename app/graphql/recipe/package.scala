package graphql

import sangria.schema.Schema

package object recipe extends graphql.HasSchema {
  val schema = new Schema(query = Queries())
}
