package app.models

import java.time.ZonedDateTime

object KnowledgeKind extends Enumeration {
  protected case class Val(override val id: Int, name: String)
    extends super.Val(id)
      with Identifiable[Int]

  def apply(s: String): KnowledgeKind = withName(s)

  implicit def fromId(id: Int): KnowledgeKind = apply(id);
  implicit def fromString(s: String): KnowledgeKind = apply(s)

  val Gossip = Val(0, "gossip")
  val Book = Val(1, "book")
  val Myth = Val(2, "myth")
  val Fable = Val(3, "fable")
}

case class Knowledge(id: Int, kind: KnowledgeKind, name: String, addedAt: ZonedDateTime, content: String)
  extends Identifiable[Int]

