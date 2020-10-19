import graphql.middleware.AuthMiddleware
import graphql.middleware.AuthMiddleware.{AuthenticationException, AuthorizationException}
import play.api.libs.json.{JsObject, JsValue}
import sangria.ast.Document
import sangria.execution.deferred.DeferredResolver
import sangria.execution.{ExceptionHandler, Executor, HandledException}
import sangria.marshalling.playJson._
import sangria.schema.{ObjectType, Schema}
import security.AppContext

import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.ClassTag

package object graphql {
  // MAGIC: Sangria can't stitch schemas with same definitions.
  // Doing it our way found by hard work
  implicit class StitchedSchema[A, B](left: Schema[A, B])(implicit t: ClassTag[B]) {
    private def nodups[T](left: TraversableOnce[T], right: TraversableOnce[T]): Vector[T] = {
//      val lv = left.toVector
//      val rv = right.toVector
//
//      val ls = left.toSet
//      val rs = right.toSet
//
//      val s = ls ++ rs

//      println((lv, rv, ls, rs, s))
//      println((lv.size, rv.size, ls.size, rs.size, s.size))
//      if (s.size < (ls.size + rs.size) || ls.size < lv.size || rs.size < rv.size) {
//        val duplicates = (lv ++ rv).toSet -- s
//        throw new IllegalArgumentException(f"Duplicates: ${duplicates}")
//      }
//      else {
//        s.toVector
//      }
      (left.toSet ++ right.toSet).toVector
    }

//    private def mergeObjectTypeOption(os: ObjectType[A, B]*): Option[ObjectType[A, B]] = {
//      mergeObjectTypeOption(os.toList)
//    }

    private def mergeObjectTypeOption(os: List[ObjectType[A, B]]): Option[ObjectType[A, B]] = {
      os match {
        case Nil => None
        case _ => Some(mergeObjectType(os))
      }
    }

    private def mergeObjectType(os: ObjectType[A, B]*): ObjectType[A, B] =
      mergeObjectType(os.toList)

    private def mergeObjectType(os: List[ObjectType[A, B]]): ObjectType[A, B] = os match {
      case Nil => throw new IllegalArgumentException("At least one ObjectType must be passed")
      case t :: Nil => t
      case l :: rest =>
        val r = mergeObjectType(rest)
        l.copy(
          fieldsFn = () => nodups(l.fieldsFn(), r.fieldsFn()).toList,
          interfaces = nodups(l.interfaces, r.interfaces).toList,
          astDirectives = nodups(l.astDirectives, r.astDirectives),
          astNodes = nodups(l.astNodes, r.astNodes)
        )
    }

    def stitch(right: Schema[A, B]): Schema[A, B] = {
      left.copy(
        query = mergeObjectType(left.query, right.query),
        mutation = mergeObjectTypeOption(List(left.mutation, right.mutation).flatten),
        subscription = mergeObjectTypeOption(List(left.subscription, right.subscription).flatten),
        additionalTypes = nodups(left.additionalTypes, right.additionalTypes).toList,
        directives = nodups(left.directives, right.directives).toList,
        validationRules = nodups(left.validationRules, right.validationRules).toList,
        astDirectives = nodups(left.astDirectives, right.astDirectives),
        astNodes = nodups(left.astNodes, right.astNodes)
      )
    }
    def +(right: Schema[A, B]): Schema[A, B] = stitch(right)
  }

  val stitchedSchema: Schema[AppContext, Unit] =
    Seq(
      auth.schema,
      user.schema,
      ingredient.schema, knowledge.schema, order.schema, product.schema, recipe.schema,
      request.schema
    ).reduce(_ + _)
  val schema: Schema[AppContext, Unit] = stitchedSchema.copy(
    query = stitchedSchema.query.copy(name = "Query"),
    mutation = stitchedSchema.mutation.map(_.copy(name = "Mutation")))

  def execute(query: Document, op: Option[String], vars: JsObject,
              ctx: AppContext)
             (implicit ec: ExecutionContext): Future[JsValue] =
    Executor.execute(
      schema = schema,
      queryAst = query,
      userContext = ctx,

      operationName = op,
      variables = vars,
      deferredResolver = Resolver,

      exceptionHandler = ErrorHandler,
      middleware = AuthMiddleware :: Nil)

  private val fetchers =
    Seq(
      ingredient.Fetchers(),
      product.Fetchers(),
      recipe.Fetchers()
    ).flatten

  private val Resolver = DeferredResolver.fetchers(fetchers: _*)
  private val ErrorHandler = ExceptionHandler {
    case (_m@_, AuthenticationException(message)) ⇒ HandledException(message)
    case (_m@_, AuthorizationException(message)) ⇒ HandledException(message)
  }
}
