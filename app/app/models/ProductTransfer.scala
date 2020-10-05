package app.models

object ProductTransferStatus extends Enumeration {
  protected final case class Val(override val id: Int, name: String)
    extends super.Val(id)
      with Identifiable[Int]

  val Produced = Val(0, "produced")
  val Transferred = Val(1, "transferred")
  val Stored = Val(2, "stored")
}

case class ProductTransfer(id: Int = -1,
                           status: ProductTransferStatus = ProductTransferStatus.Produced,
                           products: ProductList)
  extends Identifiable[Int]
