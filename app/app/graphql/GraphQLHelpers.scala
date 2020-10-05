package app.graphql

import java.time.ZonedDateTime

import app.models.User.Credentials
import app.models.{IngredientRequest, Order}
import sangria.ast.StringValue
import sangria.marshalling.{CoercedScalaResultMarshaller, FromInput}
import sangria.schema.ScalarType
import sangria.validation.Violation

import scala.util.Try

object GraphQLHelpers {
  /*
  * Marshalling helpers
  * */

  implicit val _order_input = new FromInput[Order] {
    implicit val marshaller = CoercedScalaResultMarshaller.default
    override def fromResult(node: marshaller.Node): Order = {
      val m = node.asInstanceOf[Map[String, Any]]
      Order(
        product = m("product".asInstanceOf[String]).asInstanceOf[Int],
        count = m("count").asInstanceOf[Int],
        orderedBy = m("orderedBy").asInstanceOf[String])
    }}
  implicit val _request_input = new FromInput[IngredientRequest] {
    implicit val marshaller = CoercedScalaResultMarshaller.default
    override def fromResult(node: marshaller.Node): IngredientRequest = {
      val m = node.asInstanceOf[Map[String, Any]]
      IngredientRequest(ingredients = m("ingredients").asInstanceOf[Seq[Int]].toList)
    }}
  implicit val _credentials_input = new FromInput[Credentials] {
    implicit val marshaller = CoercedScalaResultMarshaller.default
    override def fromResult(node: marshaller.Node): Credentials = {
      val m = node.asInstanceOf[Map[String, Any]]
      Credentials(
        m("id").asInstanceOf[String],
        m("password").asInstanceOf[String])
    }}
  case object DateTimeCoerceViolation extends Violation {
    override def errorMessage: String = "Error parsing DateTime"
  }

  implicit val GraphQLDateTime = ScalarType[ZonedDateTime]("ZonedDateTime",
    coerceOutput = (dt, _) => dt.toString,
    coerceInput = {
      case StringValue(s, _, _, _, _) =>
        Try(ZonedDateTime.parse(s)).toEither.fold(_ => Left(DateTimeCoerceViolation), Right(_))
      case _ => Left(DateTimeCoerceViolation)
    },
    coerceUserInput = {
      case s: String =>
        Try(ZonedDateTime.parse(s)).toEither.fold(_ => Left(DateTimeCoerceViolation), Right(_))
      case _ => Left(DateTimeCoerceViolation)
    }
  )

  // ListInputType is Seq: List needed
  implicit def seq2list[T](s: Seq[T]) = s.toList
}
