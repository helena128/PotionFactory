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
        with Identifiable[Int]

    val Produced = Val(0, "produced")
    val Transferred = Val(1, "transferred")
    val Stored = Val(2, "stored")
  }

  type Status = Status.Value
}
