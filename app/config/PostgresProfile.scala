package config

import java.time.ZonedDateTime
//import java.time{LocalDateTime, ZoneId}
import java.time.format.{DateTimeFormatter, DateTimeFormatterBuilder}
import java.time.temporal.ChronoField

import com.github.tminglei.slickpg._
import play.api.libs.json._
import slick.basic.Capability
import slick.jdbc.{JdbcCapabilities, JdbcType}

//import scala.util.Try

trait PostgresProfile extends ExPostgresProfile
                          with PgArraySupport
                          with PgDate2Support
                          with PgRangeSupport
                          with PgHStoreSupport
                          with PgPlayJsonSupport
                          with PgSearchSupport
                          with PgNetSupport
                          with PgLTreeSupport {
  def pgjson = "jsonb" // jsonb support is in postgres 9.4.0 onward; for 9.3.x use "json"

  // Add back `capabilities.insertOrUpdate` to enable native `upsert` support; for postgres 9.5+
  override protected def computeCapabilities: Set[Capability] =
    super.computeCapabilities + JdbcCapabilities.insertOrUpdate

  override val api = MyAPI

  object MyAPI extends API with ArrayImplicits
                           with DateTimeImplicits
                           with JsonImplicits
                           with NetImplicits
                           with LTreeImplicits
                           with RangeImplicits
                           with HStoreImplicits
                           with SearchImplicits
                           with SearchAssistants {
    implicit val strListTypeMapper = new SimpleArrayJdbcType[String]("text").to(_.toList)
    implicit val playJsonArrayTypeMapper =
      new AdvancedArrayJdbcType[JsValue](pgjson,
        (s) => utils.SimpleArrayUtils.fromString[JsValue](Json.parse)(s).orNull,
        (v) => utils.SimpleArrayUtils.mkString[JsValue](_.toString())(v)
      ).to(_.toList)

    override val date2TzDateTimeFormatter = new DateTimeFormatterBuilder()
      .append(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
      .optionalStart()
      .appendFraction(ChronoField.NANO_OF_SECOND,0,6,true)
      .optionalEnd()
      .appendOffset("+HH","+00")
      .toFormatter()
//    override protected val fromZonedDateTimeOrInfinity: String => ZonedDateTime = fromInfinitable(
//      LocalDateTime.MAX.atZone(ZoneId.of("UTC")), LocalDateTime.MIN.atZone(ZoneId.of("UTC")),
//      input => Try(ZonedDateTime.parse(input, date2TzDateTimeFormatter))
//        .getOrElse(ZonedDateTime.parse(input, date2TzDateTimeWithLongTzFormatter))
//    )
    override implicit val date2TzTimestamp1TypeMapper: JdbcType[ZonedDateTime] = new GenericJdbcType[ZonedDateTime]("timestamptz",
      fromZonedDateTimeOrInfinity, toZonedDateTimeOrInfinity, hasLiteralForm=false)
  }
}

object PostgresProfile extends PostgresProfile