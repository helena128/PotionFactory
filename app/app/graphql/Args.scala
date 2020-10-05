package app.graphql

import sangria.schema.{Argument, IntType, StringType}

object Args {
  val IdStr = Argument("id", StringType)
  val IdInt = Argument("id", IntType)
}
