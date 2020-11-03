package graphql.user
import graphql.auth.Tags._
import models.User
import sangria.marshalling.{CoercedScalaResultMarshaller, FromInput}
import sangria.schema._
import sangria.macros.derive._
import security.AppContext
import graphql.user.Types._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Mutations extends graphql.Mutations {
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
        m.get("phone").asInstanceOf[Option[Option[String]]].flatten.flatMap(stringToSome),
        m.get("address").asInstanceOf[Option[Option[String]]].flatten.flatMap(stringToSome),
        m("role").asInstanceOf[User.Role],
        User.Status.Active
      )
    }
  }

  val AUserCreate = Argument("user",
    InputObjectType[User]("userCreate", "User Edit Argument",
      List(
        InputField("id", StringType),
        InputField("password", StringType),
        InputField("name", StringType),
        InputField("phone", OptionInputType(StringType)),
        InputField("address", OptionInputType(StringType)),
        InputField("role", UserRoleType),
      )))


  case class UserChange(
                        id: Option[String],
                        password: Option[String],
                        name: Option[String],
                        phone: Option[String],
                        address: Option[String],
                        role: Option[User.Role] = None) {
    def change(user: User) = {
      require(id.isEmpty || user.id == id.get)

      user.copy(
        password = password.map(User.hashpw).getOrElse(user.password),
        name = name.getOrElse(user.name),
        phone = phone.orElse(user.phone),
        address = address.orElse(user.address),
        role = role.getOrElse(user.role)
      )
    }
  }

//  val AUserEdit = Argument("user",
//    InputObjectType[User]("userEdit", "User Edit Argument",
//    List(
//      InputField("id", StringType),
//      InputField("name", StringType),
//      InputField("phone", OptionInputType(StringType)),
//      InputField("address", OptionInputType(StringType)),
//      InputField("role", UserRoleType),
//    )))

  implicit val _new_user_self_input = new FromInput[UserChange] {
    implicit val marshaller = CoercedScalaResultMarshaller.default
    override def fromResult(node: marshaller.Node): UserChange = {
      val m = node.asInstanceOf[Map[String, Any]]
      def argFlat[T](name: String): Option[T] = {
        m.get(name).asInstanceOf[Option[T]]
      }
      def arg[T](name: String): Option[T] = {
        m.get(name)
          .asInstanceOf[Option[Option[T]]]
          .flatten
      }
      def stringToSome(s: String): Option[String] = s match {
        case "" => None
        case s => Some(s)
      }
      def stringArg(name: String): Option[String] = {
        arg[String](name).flatMap(stringToSome)
      }

      UserChange(
        argFlat("id"),
        stringArg("password"),
        stringArg("name"),
        stringArg("phone"),
        stringArg("address"),
        arg("role")
      )
    }
  }

  val AUserEdit = Argument("user", deriveInputObjectType[UserChange](
    ReplaceInputField("id", InputField("id", StringType)),
    ReplaceInputField("role", InputField("role", OptionInputType(UserRoleType))))
  )

  val AUserSelf = Argument("user", deriveInputObjectType[UserChange](
    ExcludeInputFields("id", "role")))
  val AUserId = Argument("userId", StringType)

  override val mutations: ObjectType[AppContext, Unit] = ObjectType("Mutation", "Schema Mutations",
    fields[AppContext, Unit](
      Field("createUser", UserType,
        arguments = List(AUserCreate),
        resolve = c => c.ctx.dao.create(c.args.arg(AUserCreate)),
        tags = List(AdminTag)
      ),
      Field("updateUser", OptionType(UserType),
        arguments = List(AUserEdit),
        resolve = c => {
          val userEdit = c.args.arg(AUserEdit)
          require(userEdit.id.nonEmpty)
          c.ctx.dao.getUser(userEdit.id.get).flatMap(
            _.map(user => c.ctx.dao.update(userEdit.change(user))).getOrElse(Future(None)))
        },
        tags = List(AdminTag)
      ),
      Field("updateUserSelf", UserType,
        arguments = List(AUserSelf),
        resolve = c => {
          val user = c.args.arg(AUserSelf).change(c.ctx.currentUser.get)
          UpdateCtx(c.ctx.dao.update(user).map(_.get))(u => c.ctx.copy(currentUser = Some(u)))
        },
        tags = List(ActiveTag)
      ),
      Field("deactivateUser", BooleanType,
        arguments = List(AUserId),
        resolve = c => c.ctx.dao.deactivateUser(c.args.arg(AUserId)),
        tags = List(AdminTag)
      )
    )
  )
}
