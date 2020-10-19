import config.DBSchema.setup
import config.PostgresProfile.api._

import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.duration._

package object repository {
  val dao =
    {
      val dao = new DAO(Database.forConfig("pg"))

      val tableCount =
        Await.result(
          dao.run(sql"""SELECT count(*) from pg_catalog.pg_tables where schemaname='public';""".as[Int]),
          1 minute)
          .head

      println("Tables: " + tableCount)

      tableCount match {
        case 0 =>
          println("Running setup")
          Await.result(dao.run(setup), 1 minute)
        case _ =>
          println("Database is already in place")
          Future(())
      }

      println("All set")

      dao
    }
}
