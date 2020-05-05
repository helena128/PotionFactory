package controllers

import app.{AppContext, DBSchema}
import javax.inject._
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import sangria.parser._
import sangria.ast.Document
import sangria.marshalling.playJson._
import sangria.execution._
import sangria.renderer.SchemaRenderer
import app.graphql._

@Singleton
class GraphQLController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {
  private val renderedSchema =
    Seq(Schema())
      .map(_.toAst)
      .reduce(_+_)
      .renderPretty

  val schema: Action[AnyContent] = Action { Ok(renderedSchema) }

  def graphql: Action[JsValue] = Action.async(parse.json) { request ⇒
    println("New GraphQL query: " + request.body)

    val query = (request.body \ "query").as[String]
    val operation = (request.body \ "operationName").asOpt[String]
    val variables = (request.body \ "variables")
      .toOption.map({
        case JsString(none) if (!Seq("", "null").contains(none.trim)) => Json.obj()
        case JsString(vars) => Json.parse(vars).as[JsObject]
        case obj: JsObject ⇒ obj
        case _ ⇒ Json.obj()
      })
      .getOrElse(Json.obj())

    QueryParser
      .parse(query)
      .map(executeGraphQLQuery(_, operation, variables))
      .recover {case e => Future.successful(BadRequest(Json.obj("error" -> e.getMessage)))}
      .get
  }

  def executeGraphQLQuery(query: Document, op: Option[String], vars: JsObject): Future[Result] =
    Executor.execute(
      schema = Schema(),
      queryAst = query,
      userContext = AppContext(GraphQLController.dao),

      operationName = op,
      variables = vars,
      deferredResolver = Schema.Resolver
//      exceptionHandler = TODO
//      deferredResolver = GraphQLSchema.Resolver,
//      exceptionHandler = GraphQLSchema.ErrorHandler,
//      middleware = AuthMiddleware :: Nil
    )
      .map(Ok(_))
      .recover {
        case error: QueryAnalysisError ⇒ BadRequest(error.resolveError)
        case error: ErrorWithResolver ⇒ InternalServerError(error.resolveError)
      }

}

object GraphQLController {
  val dao = DBSchema.createDatabase
}

