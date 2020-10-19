package controllers

import email._
import java.util.UUID

import javax.inject.{Inject, Singleton}
import play.api.mvc._
import repository.dao

import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class ConfirmationController @Inject()(cc: ControllerComponents,
                                    mailer: MailService
                                      ) extends AbstractController(cc) {
  val send: Action[AnyContent] = Action {
//    mailer.sendConfirmation("Yaroslav", "rogovyaroslav@gmail.com", "123")
    Ok("ok")
  }

  def confirm(id: String): Action[AnyContent] = Action.async(parse.default) { _ =>
    dao.confirm(UUID.fromString(id)).map {
      case Some(user) =>
        mailer.sendWelcome(user)
        Redirect("/confirmation/confirmed")
      case None =>
        Redirect("/confirmation/invalid")
    }
  }
}
