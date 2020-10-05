package app.graphql

import app.graphql.ingredient.Queries
import sangria.schema.Schema

package object knowledge extends app.graphql.HasSchema {
  val schema = new Schema(query = Queries())
}
