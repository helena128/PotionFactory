package app.graphql

import sangria.schema.Schema

package object recipe extends app.graphql.HasSchema {
  val schema = new Schema(query = Queries())
}
