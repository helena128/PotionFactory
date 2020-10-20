package graphql.auth

import graphql.auth.Tags.NotAuthenticatedTag
import graphql.user.Types._
import models.{AccountConfirmation, User}
import models.User.Credentials
import sangria.macros.derive.{InputObjectTypeName, deriveInputObjectType}
import sangria.marshalling.{CoercedScalaResultMarshaller, FromInput}
import sangria.schema._
import security.AppContext

import scala.concurrent.ExecutionContext.Implicits.global

object Mutations extends graphql.Mutations {
  implicit val _credentials_input = new FromInput[Credentials] {
    implicit val marshaller = CoercedScalaResultMarshaller.default
    override def fromResult(node: marshaller.Node): Credentials = {
      val m = node.asInstanceOf[Map[String, Any]]
      Credentials(
        m("id").asInstanceOf[String],
        m("password").asInstanceOf[String])
    }}
  implicit val _new_user_input = new FromInput[User] {
    implicit val marshaller = CoercedScalaResultMarshaller.default
    override def fromResult(node: marshaller.Node): User = {
      def stringToSome(s: String): Option[String] = s match {
        case "" => None
        case s => Some(s)
      }
      val m = node.asInstanceOf[Map[String, Any]]
      User.newUser(
        m("id").asInstanceOf[String],
        m("password").asInstanceOf[String],
        m("name").asInstanceOf[String],
        m("email").asInstanceOf[String],
        m.get("phone").asInstanceOf[Option[String]].flatMap(stringToSome),
        m.get("address").asInstanceOf[Option[String]].flatMap(stringToSome)
      )
    }
  }

  val ACredentials = Argument("credentials",
    deriveInputObjectType[User.Credentials](InputObjectTypeName("Credentials")))
  val AUser = Argument("user",
    InputObjectType[User]("user", "User Argument",
      List(
        InputField("id", StringType),
        InputField("password", StringType),
        InputField("name", StringType),
        InputField("email", StringType),
        InputField("phone", OptionInputType(StringType)),
        InputField("address", OptionInputType(StringType)),
      )))

  override val mutations = ObjectType("Mutation", "Schema Mutations",
    fields[AppContext, Unit](
      Field("signup", UserType,
        arguments = List(AUser),
        resolve = c => {
          UpdateCtx(
            c.ctx.dao.create(c.arg(AUser))
              .flatMap(user =>
                c.ctx.dao.create(AccountConfirmation(userId = user.id))
                  .map((user, _))
              )
              .map {
                case (user, confirmation) =>
                  val messageId = c.ctx.mailer.sendConfirmation(user, confirmation)
                  println(f"Sent confirmation ($messageId) for ${user.email}: $confirmation")
                  user
              })(user => c.ctx.copy(currentUser = Some(user)))
        },
        tags = List(NotAuthenticatedTag)),
      Field("login",
        OptionType(UserType),
        arguments = ACredentials :: Nil,
        resolve = c =>
          UpdateCtx(c.ctx.login(c.arg(ACredentials))){
            case Some(user) => c.ctx.copy(currentUser = Some(user))
            case None => c.ctx
          }
      ),
      Field("logout",
        BooleanType,
        resolve = c =>
          UpdateCtx(c.ctx.logout()){
            if (_) c.ctx.copy(currentUser = None)
            else c.ctx
          }
      )))
}
