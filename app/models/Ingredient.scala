package models

import java.time.ZonedDateTime

case class Ingredient(id: Int, name: String, addedAt: ZonedDateTime, description: String, count: Int)
  extends Identifiable[Int]
