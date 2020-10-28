package models

import java.time.ZonedDateTime

case class Order(id: Int = -1,
                 product: Int,
                 count: Int,
                 orderedBy: String,
                 createdAt: ZonedDateTime = ZonedDateTime.now())
  extends Identifiable[Int]

