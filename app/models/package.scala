import java.io.Serializable

package object models {
  type IngredientList = List[Int] with Serializable
  type ProductTag = String with Serializable
  type ProductTags = List[ProductTag] with Serializable
  type ProductList = List[Int] with Serializable
}
