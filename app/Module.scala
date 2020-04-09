import java.io.{BufferedWriter, File, FileWriter}
import java.nio.file.Files

import com.google.inject.AbstractModule

import scala.reflect.io.Directory

/**
 * This class is a Guice module that tells Guice how to bind several
 * different types. This Guice module is created when the Play
 * application starts.

 * Play will automatically use any class called `Module` that is in
 * the root package. You can create modules in other locations by
 * adding `play.modules.enabled` settings to the `application.conf`
 * configuration file.
 */
class Module extends AbstractModule {
  override def configure() = {
    renderSchemaToFile()
  }

  private def renderSchemaToFile(): Unit = {
    val schemaFile = new File("graphql/schema.graphql")
    Option(schemaFile.getParent).map(new File(_).mkdirs())
    schemaFile.createNewFile()
    new FileWriter(schemaFile).write(graphql.Schema.render)
  }
}
