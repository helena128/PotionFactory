import config.DBSchema.{schema, setup}
import slick.jdbc.H2Profile.api._

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits._

import scala.language.postfixOps
import scala.concurrent.duration._

package object repository {
  val dao =
    {
      val dao = new DAO(Database.forConfig("h2mem"))

      // TODO: redo this part
      dao.run(sql"""SHOW TABLES""".as[String])
        .andThen({case t =>
          if (t.get.nonEmpty) Await.result(dao.run(schema.dropIfExists), 10 seconds)
          Await.result(dao.run(setup), 10 seconds)
        })

      dao
    }
}
