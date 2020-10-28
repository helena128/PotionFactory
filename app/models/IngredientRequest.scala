package models

import java.time.ZonedDateTime

case class IngredientRequest(id: Int = -1,
                             status: IngredientRequest.Status = IngredientRequest.Status.Open,
                             ingredients: IngredientList,
                             createdAt: ZonedDateTime = ZonedDateTime.now()
                            )
  extends Identifiable[Int]

object IngredientRequest {
  val tupled = (IngredientRequest.apply _).tupled

  object Status extends Enumeration {
    protected final case class Val(override val id: Int, name: String)
      extends super.Val(id)
        with Identifiable[Int] {
      override def toString(): String = name
    }

    val Open     = Val(0, "open")
    val Transfer = Val(1, "transfer")
    val Received = Val(2, "received")
  }

  type Status = Status.Value
}