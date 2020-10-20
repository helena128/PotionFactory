/**
  * Frontend build commands.
  * Change these if you are using some other package manager. i.e: Yarn
  */
object FrontendCommands {
  private val outputPath = "../public" // Relative to `ui` directory
  val dependencyInstall: String = "npm install"
  val test: String = "npm run test:ci"
  val watch: String = f"npm run watch -- --output-path=$outputPath"
  val build: String = f"npm run build:prod -- --output-path=$outputPath"
}