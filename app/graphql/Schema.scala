package graphql

import schema._
import sangria.schema._
import sangria.macros.derive._
import sangria.renderer.SchemaRenderer

object Schema {
  def apply(): Schema[UserRepo, Unit] = schema
  def render: String = SchemaRenderer.renderSchema(schema)

  /*
   * Common
   */

  private val IdentifiableType = InterfaceType(
    "Identifiable",
    "Entity that can be identified",
    fields[Unit, Identifiable](Field("id", StringType, resolve = _.value.id)))


  /*
   * User
   */

  private implicit val UserRoleType = deriveEnumType[UserRole](EnumTypeName("UserRole"))

  private implicit val UserType = deriveObjectType[Unit, User](
    Interfaces(IdentifiableType),
    ObjectTypeName("User"),
    ObjectTypeDescription("User account and info"),
  )

  private val Id = Argument("id", StringType)

  private val QueryType = ObjectType("Query", "User Queries",
    fields[UserRepo, Unit](
      Field("user", OptionType(UserType),
        description = Some("Returns a user with specific `id`."),
        arguments = Id :: Nil,
        resolve = c ⇒ c.ctx.user(c arg Id)),

      Field("users", ListType(UserType),
        description = Some("Returns a list of all available products."),
        resolve = _.ctx.users)))

//  private val schema = new Schema(QueryType)
  val schema = new Schema(QueryType)


  //  def execute_sample_query = {
//    //    import schema._
//    import sangria.macros._
//    val query =
//      graphql"""
//          query QueryForUsers {
//            user(id: "user-id-for-client") {
//              username
//              email
//              role
//            }
//            users {
//              id
//              email
//              role
//            }
//          }
//        """
//
//    import sangria.execution._
//    import sangria.marshalling.playJson._
//    import scala.concurrent.ExecutionContext
//    implicit val ec = ExecutionContext.global
//    Executor.execute(UserSchema.schema, query, new UserRepo)
//  }
}
