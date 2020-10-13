package controllers

import javax.inject.{Inject, Singleton}
import play.api.mvc._
import email._

@Singleton
class ConfirmationController @Inject()(mailer: MailService, cc: ControllerComponents) extends AbstractController(cc) {
  val send: Action[AnyContent] = Action {
    mailer.sendConfirmation("Yaroslav", "rogovyaroslav@gmail.com", "123")
    Ok("ok")
  }
}
