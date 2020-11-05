package email

//import java.io.{File, PrintWriter}

import email.templates.html._
import javax.inject.{Inject, Provider}
import models.{AccountConfirmation, Order, User}
import play.api.libs.mailer._

class MailService @Inject()(mailerClient: MailerClient, smtpConfigurationProvider: Provider[SMTPConfiguration]) {
  type MessageID = String
  val noreply = "Potions <noreply@potions.ml>"

  def isMocked = smtpConfigurationProvider.get().mock

  def sendConfirmation(user: User, confirmation: AccountConfirmation): MessageID = {
    println("Sending confirmation to " + user)
    send(
      Email(
        "Confirm your account",
        noreply,
        Seq(user.email),
        bodyHtml = Some(Confirmation(user, confirmation).body)
      )
    )
  }

  def sendWelcome(user: User): MessageID = {
    send(
      Email(
        "Welcome to Potions",
        noreply,
        Seq(user.email),
        bodyHtml = Some(Welcome(user).body)
      )
    )
  }

  def sendOrderChange(user: User, order: Order): MessageID = {
    send(
      Email(
        f"Your Order #${order.id} Status has changed",
        noreply,
        Seq(user.email),
        bodyHtml = Some(OrderChange(user, order).body)
      )
    )
  }

  private def send(email: Email): MessageID = {
    mailerClient.send(email)
  }
}
