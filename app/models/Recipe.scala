package models

case class Recipe(id: Int = -1, name: String, description: String, ingredients: IngredientList)
  extends Identifiable[Int]

