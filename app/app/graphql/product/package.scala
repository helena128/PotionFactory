package app.graphql

import sangria.schema.Schema

package object product extends app.graphql.HasSchema {
  val schema = new Schema(query = Queries())
}
