package controllers

import javax.inject.{Inject, Singleton}
import play.api.mvc._
import email._

@Singleton
class ConfirmationController @Inject()(mailer: MailerService, cc: ControllerComponents) extends AbstractController(cc) {
  val send: Action[AnyContent] = Action {
    Ok("ok")
  }
}
