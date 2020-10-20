package graphql.user
import models.User
import sangria.marshalling.{CoercedScalaResultMarshaller, FromInput}
import sangria.schema._
import security.AppContext

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

  override val mutations: ObjectType[AppContext, Unit] = ObjectType("Mutation", "Schema Mutations",
    fields[AppContext, Unit](
      Field("createUser", UserType,
        arguments = List(AUser),
        resolve = c => c.ctx.dao.create(c.args.arg(AUser))
      )))
}
