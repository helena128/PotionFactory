package models


case class ProductTransfer(id: Int = -1,
                           status: ProductTransfer.Status = ProductTransfer.Status.Produced,
                           products: ProductList)
  extends Identifiable[Int]

object ProductTransfer {
  val tupled = (ProductTransfer.apply _).tupled

  object Status extends Enumeration {
    protected final case class Val(override val id: Int, name: String)
      extends super.Val(id)
        with Identifiable[Int] {
      override def toString(): String = name
    }

    val Produced = Val(0, "produced")
    val Transfer = Val(1, "transfer")
    val Stored = Val(2, "stored")
  }

  type Status = Status.Value
}
