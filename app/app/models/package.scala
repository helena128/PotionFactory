package app

import java.io.Serializable

package object models {
  type UserRole = UserRole.Value
  type KnowledgeKind = KnowledgeKind.Value
  type IngredientList = List[Int] with Serializable
  type ProductTag = String with Serializable
  type ProductTags = List[ProductTag] with Serializable
  type ProductList = List[Int] with Serializable
  type ProductTransferStatus = ProductTransferStatus.Value
}
