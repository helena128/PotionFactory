package models

import java.time.ZonedDateTime
import java.util.UUID

case class AccountConfirmation(id: UUID = UUID.randomUUID(), userId: String,
                               status: AccountConfirmation.Status = AccountConfirmation.Status.Pending,
                               activeUntil: ZonedDateTime = ZonedDateTime.now(),
                               createdAt: ZonedDateTime = ZonedDateTime.now(),
                               modifiedAt: ZonedDateTime = ZonedDateTime.now())
object AccountConfirmation {
  type Status = Status.Value

  object Status extends Enumeration {
    protected case class Val(override val id: Int, name: String)
      extends super.Val(id)
        with Identifiable[Int] {
    }

    def apply(s: String): Status = withName(s)

    implicit def fromId(id: Int): Status = apply(id);
    implicit def fromString(s: String): Status = apply(s)

    val Pending = Value(0, "pending")
    val Fulfilled = Value(1, "fulfilled")
    val Expired = Value(2, "expired")
    val Cancelled = Value(3, "cancelled")
  }

  val tupled = (AccountConfirmation.apply _).tupled
}
