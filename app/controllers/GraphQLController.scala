package controllers

import javax.inject._
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}
import sangria.parser._
import sangria.ast.Document
import sangria.marshalling.playJson._
import sangria.execution._
import sangria.renderer.SchemaRenderer
import graphql._
import schema.UserRepo

@Singleton
class GraphQLController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {
  implicit val ec: ExecutionContext = ExecutionContext.global

  def schema: Action[AnyContent] = Action { Ok(Schema.render) }

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
    Executor.execute(Schema(), query, new UserRepo, operationName = op, variables = vars)
      .map(Ok(_))
      .recover {
        case error: QueryAnalysisError ⇒ BadRequest(error.resolveError)
        case error: ErrorWithResolver ⇒ InternalServerError(error.resolveError)
      }

}

