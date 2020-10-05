package graphql.auth

import graphql.auth.Tags.NotAuthenticatedTag
import sangria.schema.{Argument, BooleanType, Field, ObjectType, OptionType, UpdateCtx, fields}
import graphql.user.Types._
import models.User
import models.User.Credentials
import sangria.macros.derive.{InputObjectTypeName, deriveInputObjectType}
import sangria.marshalling.{CoercedScalaResultMarshaller, FromInput}
import security.AppContext

object Mutations extends graphql.Mutations {
  implicit val _credentials_input = new FromInput[Credentials] {
    implicit val marshaller = CoercedScalaResultMarshaller.default
    override def fromResult(node: marshaller.Node): Credentials = {
      val m = node.asInstanceOf[Map[String, Any]]
      Credentials(
        m("id").asInstanceOf[String],
        m("password").asInstanceOf[String])
    }}

  val ACredentials = Argument("credentials",
    deriveInputObjectType[User.Credentials](InputObjectTypeName("Credentials")))

  override val mutations = ObjectType("Mutation", "Schema Mutations",
    fields[AppContext, Unit](
      Field("login",
        OptionType(UserType),
        arguments = ACredentials :: Nil,
        resolve = c =>
          UpdateCtx(c.ctx.login(c.arg(ACredentials))){
            case Some(user) => c.ctx.copy(currentUser = Some(user))
            case None => c.ctx
          },
        tags = NotAuthenticatedTag :: Nil
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
