package email

import java.io.{File, PrintWriter}

import email.templates.html.Confirmation
import javax.inject.Inject
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

  def sendConfirmation(name: String, email: String, confirmId: String) = {
//    println()
    val filename = "/tmp/test.html"
    new PrintWriter((new File(filename))).write(Confirmation(name, confirmId).body)
//    sys.process.Process("open", Seq(filename))!

    send(
      Email(
        "Confirm your account",
        noreply,
        Seq(email),
        bodyHtml = Some(Confirmation(name, confirmId).body)
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