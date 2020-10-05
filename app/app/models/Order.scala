package app.models

case class Order(id: Int = -1, product: Int, count: Int, orderedBy: String)
  extends Identifiable[Int]

