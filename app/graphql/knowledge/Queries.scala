package graphql.knowledge

import graphql.auth.Tags._
import Types._
import sangria.schema.{Argument, Field, IntType, ListType, ObjectType, StringType, fields}
import graphql.Args._
import security.AppContext

object Queries extends graphql.Queries {
  val SearchString = Argument("string", StringType)
  val SearchLimit = Argument("limit", IntType)
  val SearchLookaround = Argument("lookaround", IntType)

  val queries = ObjectType("KnowledgeQuery", "Knowledge Queries",
    fields[AppContext, Unit](
      Field("searchKnowledge", ListType(KnowledgeType),
        arguments = SearchString :: SearchLimit :: SearchLookaround :: Nil,
        resolve = c => c.ctx.dao.searchKnowledge(c.arg(SearchString), c.arg(SearchLimit), c.arg(SearchLookaround)),
        tags = FairyTag :: Nil),
      Field("getKnowledge", KnowledgeType,
        arguments = IdInt :: Nil,
        resolve = c => c.ctx.dao.getKnowledge(c.arg(IdInt)),
        tags = FairyTag :: Nil)))
}
