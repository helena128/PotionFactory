package graphql.knowledge

import graphql.Args._
import graphql.auth.Tags._
import graphql.knowledge.Types._
import sangria.schema.{Argument, Field, IntType, ListType, ObjectType, StringType, fields}
import security.AppContext

object Queries extends graphql.Queries {
  val SearchString = Argument("string", StringType)
  val SearchLimit = Argument("limit", IntType)
  val SearchLookaround = Argument("lookaround", IntType)

  private val tags = List(FairyTag)

  val queries = ObjectType("KnowledgeQuery", "Knowledge Queries",
    fields[AppContext, Unit](
      Field("searchKnowledge", ListType(KnowledgeType),
        arguments = SearchString :: SearchLimit :: SearchLookaround :: Nil,
        resolve = c => c.ctx.dao.searchKnowledge(c.arg(SearchString), c.arg(SearchLimit), c.arg(SearchLookaround)),
        tags = tags),
      Field("getKnowledge", KnowledgeType,
        arguments = IdInt :: Nil,
        resolve = c => c.ctx.dao.getKnowledge(c.arg(IdInt)),
        tags = tags)))
}
