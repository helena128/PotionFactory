package graphql.knowledge

import models.Knowledge
import models.Knowledge.Kind
import sangria.macros.derive.{EnumTypeName, IncludeValues, Interfaces, ObjectTypeName, deriveEnumType, deriveObjectType}
import graphql.Types._
import security.AppContext

object Types {
  implicit val KnowledgeKindType = deriveEnumType[Knowledge.Kind.Value](EnumTypeName("KnowledgeKind"),
    IncludeValues("Gossip", "Book", "Myth", "Fable"))
  implicit val KnowledgeType = deriveObjectType[AppContext, Knowledge](
    Interfaces(IdentifiableWithIntType),
    ObjectTypeName("Knowledge"))
}
