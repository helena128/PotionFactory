package graphql.user
import graphql.auth.Tags.{AdminTag, AuthenticatedTag}
import models.User
import sangria.marshalling.{CoercedScalaResultMarshaller, FromInput}
import sangria.schema._
import sangria.macros.derive._
import security.AppContext
import graphql.user.Types._
import scala.concurrent.ExecutionContext.Implicits.global

object Mutations extends graphql.Mutations {
  implicit val _new_user_input = new FromInput[User] {
    implicit val marshaller = CoercedScalaResultMarshaller.default
    override def fromResult(node: marshaller.Node): User = {
      def stringToSome(s: String): Option[String] = s match {
        case "" => None
        case s => Some(s)
      }
      val m = node.asInstanceOf[Map[String, Any]]

      User(
        m("id").asInstanceOf[String],
        m("password").asInstanceOf[String],
        m("name").asInstanceOf[String],
        m("email").asInstanceOf[String],
        m.get("phone").asInstanceOf[Option[String]].flatMap(stringToSome),
        m.get("address").asInstanceOf[Option[String]].flatMap(stringToSome),
        User.Role.fromString(m("role").asInstanceOf[String]),
        User.Status.fromString(m("status").asInstanceOf[String])
      )
    }
  }

  case class UserChange(password: Option[String],
                        name: Option[String],
                        email: Option[String],
                        phone: Option[String],
                        address: Option[String]) {
    def change(user: User) =
      user.copy(
        password = password.map(User.hashpw).getOrElse(user.password),
        name = name.getOrElse(user.name),
        email = email.getOrElse(user.email),
        phone = phone.orElse(user.phone),
        address = address.orElse(user.address))
  }

  val AUser = Argument("user",
    InputObjectType[User]("user", "User Argument",
    List(
      InputField("id", StringType),
      InputField("password", StringType),
      InputField("name", StringType),
      InputField("email", StringType),
      InputField("phone", OptionInputType(StringType)),
      InputField("address", OptionInputType(StringType)),
      InputField("role", StringType),
      InputField("status", StringType)
    )))

  implicit val _new_user_self_input = new FromInput[UserChange] {
    implicit val marshaller = CoercedScalaResultMarshaller.default
    override def fromResult(node: marshaller.Node): UserChange = {
      val m = node.asInstanceOf[Map[String, Any]]
      def stringToSome(s: String): Option[String] = s match {
        case "" => None
        case s => Some(s)
      }
      def arg(name: String): Option[String] = {
        m.get(name)
          .asInstanceOf[Option[Option[String]]]
          .flatten
          .flatMap(stringToSome)
      }

      UserChange(
        arg("password"),
        arg("name"),
        arg("email"),
        arg("phone"),
        arg("address")
      )
    }
  }

  val AUserSelf = Argument("user", deriveInputObjectType[UserChange]())
  val AUserId = Argument("userId", StringType)

  override val mutations: ObjectType[AppContext, Unit] = ObjectType("Mutation", "Schema Mutations",
    fields[AppContext, Unit](
      Field("createUser", UserType,
        arguments = List(AUser),
        resolve = c => c.ctx.dao.create(c.args.arg(AUser)),
        tags = List(AdminTag)
      ),
      Field("updateUser", OptionType(UserType),
        arguments = List(AUser),
        resolve = c => c.ctx.dao.update(c.args.arg(AUser)),
        tags = List(AdminTag)
      ),
      Field("updateUserSelf", UserType,
        arguments = List(AUserSelf),
        resolve = c => {
          val user = c.args.arg(AUserSelf).change(c.ctx.currentUser.get)
          UpdateCtx(c.ctx.dao.update(user).map(_.get))(u => c.ctx.copy(currentUser = Some(u)))
        },
        tags = List(AuthenticatedTag)
      ),
      Field("deactivateUser", BooleanType,
        arguments = List(AUserId),
        resolve = c => c.ctx.dao.deactivateUser(c.args.arg(AUserId)),
        tags = List(AdminTag)
      )
    )
  )
}
