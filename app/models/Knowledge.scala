package models

import java.time.ZonedDateTime

case class Knowledge(id: Int, kind: Knowledge.Kind, name: String, addedAt: ZonedDateTime, content: String)
  extends Identifiable[Int]

object Knowledge {
  val tupled = (Knowledge.apply _).tupled

  object Kind extends Enumeration {
    protected case class Val(override val id: Int, name: String)
      extends super.Val(id)
        with Identifiable[Int]

    def apply(s: String): Kind = withName(s)

    implicit def fromId(id: Int): Kind = apply(id);
    implicit def fromString(s: String): Kind = apply(s)

    val Gossip = Val(0, "gossip")
    val Book = Val(1, "book")
    val Myth = Val(2, "myth")
    val Fable = Val(3, "fable")
  }

  type Kind = Kind.Value
}
