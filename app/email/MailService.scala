package email

//import java.io.{File, PrintWriter}

import email.templates.html._
import javax.inject.Inject
import models.{AccountConfirmation, User}
import play.api.libs.mailer._

class MailService @Inject()(mailerClient: MailerClient) {
  type MessageID = String
//  def sendEmail() = {
//    val email = Email(
//      "Simple email",
//      "Potions Factory <noreply@potions.ml>",
//      Seq("yrogov <rogovyaroslav@gmail.com>"),
      // adds attachment
//      attachments = Seq(
//        AttachmentFile("attachment.pdf", new File("/some/path/attachment.pdf")),
//        // adds inline attachment from byte array
//        AttachmentData("data.txt", "data".getBytes, "text/plain", Some("Simple data"), Some(EmailAttachment.INLINE)),
//        // adds cid attachment
//        AttachmentFile("image.jpg", new File("/some/path/image.jpg"), contentId = Some(cid))
//      ),
      // sends text, HTML or both...
//      bodyText = Some("A text message"),
//      bodyHtml = Some(s"""<html><body><p><h1>Hello, World!</h1></p></body></html>""")
//    )
//    mailerClient.send(email)
//  }

  val noreply = "Potions <noreply@potions.ml>"

  def sendConfirmation(user: User, confirmation: AccountConfirmation): MessageID = {
//    println()
//    val filename = "/tmp/test.html"
//    new PrintWriter((new File(filename))).write(Confirmation(user, confirmation).body)
//    sys.process.Process("open", Seq(filename))!

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

  private def send(email: Email): MessageID = {
    mailerClient.send(email)
  }
}

//object MailerService {
//  private val mailer = new MailerService()
//}