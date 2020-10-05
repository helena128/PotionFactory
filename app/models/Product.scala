package models

case class Product(id: Int = -1,
                   name: String,
                   tags: ProductTags,
                   description: String,
                   recipe: Int,
                   count: Int,
                   basePrice: Double)
  extends Identifiable[Int]

