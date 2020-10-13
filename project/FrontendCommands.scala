/**
  * Frontend build commands.
  * Change these if you are using some other package manager. i.e: Yarn
  */
object FrontendCommands {
  val dependencyInstall: String = "npm install"
  val test: String = "npm run test:ci"
  val watch: String = "npm run watch"
  val build: String = "npm run build:prod"
}