package models

case class IngredientRequest(id: Int = -1, ingredients: IngredientList)
  extends Identifiable[Int]