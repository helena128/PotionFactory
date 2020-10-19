package controllers

import java.util.UUID

import javax.inject._
import play.api.libs.json._
import play.api.mvc._
import repository.dao
import sangria.execution._
import sangria.marshalling.playJson._
import sangria.parser._
import security.AppContext

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

@Singleton
class GraphQLController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {
  private val renderedSchema = graphql.schema.renderPretty

  val schema: Action[AnyContent] = Action { Ok(renderedSchema) }

  val executeRequest: Action[JsValue] = Action.async(parse.json) { request ⇒
    println("New GraphQL query: " + request.body)
    println("Session: " + request.session)

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

    // TODO: Redo sessioning
    val sessionIdOpt =
      request.session.get("session_id")
      .filter(s => Await.result(dao.isSessionActive(s), Duration.Inf))

    val sessionId = sessionIdOpt.getOrElse(UUID.randomUUID().toString)
    val session =
      sessionIdOpt
        .map(_ => request.session)
        .getOrElse(request.session + ("session_id" -> sessionId))

    val result =
      QueryParser
        .parse(query)
        .map(
          graphql.execute(_, operation, variables, AppContext(sessionId, dao))
          .map(Ok(_).withSession(session))
          .recover {
            case error: QueryAnalysisError ⇒ BadRequest(error.resolveError)
            case error: ErrorWithResolver ⇒ InternalServerError(error.resolveError)
          }
        )
        .recover {case e => Future.successful(BadRequest(Json.obj("error" -> e.getMessage)))}
        .get

    println()

    result
  }
}
