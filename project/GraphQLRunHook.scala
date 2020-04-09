import play.sbt.PlayRunHook

import scala.sys.process.Process

object GraphQLRunHook {
  def apply(): PlayRunHook = {
    object Hook extends PlayRunHook {

//      var process: Option[Process] = None

      /**
       * Change the commands in `FrontendCommands.scala` if you want to use Yarn.
       */
      var install: String = FrontendCommands.dependencyInstall
      var run: String = FrontendCommands.serve

      // Windows requires npm commands prefixed with cmd /c
      if (System.getProperty("os.name").toLowerCase().contains("win")){
        install = "cmd /c" + install
        run = "cmd /c" + run
      }

      /**
       * Executed before play run start.
       * Run npm install if node modules are not installed.
       */
      override def beforeStarted(): Unit = {
        if (!(base / "ui" / "node_modules").exists()) Process(install, base / "ui").!
      }

      /**
       * Executed after play run start.
       * Run npm start
       */
      override def afterStarted(): Unit = {
        process = Option(
          Process(run, base / "ui").run
        )
      }

      /**
       * Executed after play run stop.
       * Cleanup frontend execution processes.
       */
      override def afterStopped(): Unit = {
        process.foreach(_.destroy())
        process = None
      }
    }
    Hook
  }
}
