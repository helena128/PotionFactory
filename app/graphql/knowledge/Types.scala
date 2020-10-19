package graphql.knowledge

import graphql.Types._
import models.Knowledge
import models.Knowledge._
import sangria.macros.derive._
import security.AppContext

object Types {
  implicit val KnowledgeKindType = deriveEnumType[Knowledge.Kind.Value](EnumTypeName("KnowledgeKind"),
    IncludeValues("Gossip", "Book", "Myth", "Fable"))
  implicit val KnowledgeType = deriveObjectType[AppContext, Knowledge](
    Interfaces(IdentifiableWithIntType),
    ObjectTypeName("Knowledge"))
}
