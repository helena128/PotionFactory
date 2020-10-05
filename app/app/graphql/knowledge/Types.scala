package app.graphql.knowledge

import app.AppContext
import app.models.{Knowledge, KnowledgeKind}
import sangria.macros.derive.{EnumTypeName, IncludeValues, Interfaces, ObjectTypeName, deriveEnumType, deriveObjectType}
import app.graphql.Types._

object Types {
  implicit val KnowledgeKindType = deriveEnumType[KnowledgeKind.Value](EnumTypeName("KnowledgeKind"),
    IncludeValues("Gossip", "Book", "Myth", "Fable"))
  implicit val KnowledgeType = deriveObjectType[AppContext, Knowledge](
    Interfaces(IdentifiableWithIntType),
    ObjectTypeName("Knowledge"))
}
