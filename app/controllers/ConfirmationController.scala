package controllers

import email._
import javax.inject.{Inject, Singleton}
import play.api.mvc._

@Singleton
class ConfirmationController @Inject()(mailer: MailService, cc: ControllerComponents) extends AbstractController(cc) {
  val send: Action[AnyContent] = Action {
    mailer.sendConfirmation("Yaroslav", "rogovyaroslav@gmail.com", "123")
    Ok("ok")
  }
}
