package app.graphql.knowledge

import app.AppContext
import app.graphql.auth.Tags._
import Types._
import sangria.schema.{Argument, Field, IntType, ListType, ObjectType, StringType, fields}
import app.graphql.Args._

object Queries extends app.graphql.Queries {
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
        resolve = c => c.ctx.dao.getKnowledge(c.arg(IdInt)))))
}
