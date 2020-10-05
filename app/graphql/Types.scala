package graphql

import java.time.ZonedDateTime

import models.Identifiable
import sangria.ast.StringValue
import sangria.schema.{Field, IntType, InterfaceType, ScalarType, StringType, fields}
import sangria.validation.Violation
import security.AppContext

import scala.util.Try

object Types {
  implicit val IdentifiableWithStringType = InterfaceType(
    "IdentifiableWithString",
    "Entity that can be identified with String",
    fields[AppContext, Identifiable[String]](Field("id", StringType, resolve = _.value.id)))

  implicit val IdentifiableWithIntType = InterfaceType(
    "IdentifiableWithInt",
    "Entity that can be identified with Integer",
    fields[AppContext, Identifiable[Int]](Field("id", IntType, resolve = _.value.id)))


  private case object DateTimeCoerceViolation extends Violation {
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
}
