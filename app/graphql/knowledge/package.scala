package graphql

import graphql.ingredient.Queries
import sangria.schema.Schema

package object knowledge extends graphql.HasSchema {
  val schema = new Schema(query = Queries())
}