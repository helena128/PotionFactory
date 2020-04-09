package schema

sealed abstract class UserRole(private val id: Int, name: String)
object UserRole {
  final object Admin extends UserRole(0, "admin")
  final object Client extends UserRole(1, "client")
  final object WorkshopManager extends UserRole(2, "workshop-manager")
  final object WarehouseManager extends UserRole(3, "warehouse-manager")

  def apply(id: Integer): UserRole = {
    case 0 => Admin
    case 1 => Client
    case 2 => WorkshopManager
    case 3 => WarehouseManager
    case id => throw new IllegalArgumentException("Bad UserRole Id: " + id)
  }
  def apply(s: String): UserRole = {
    case "admin" => Admin
    case "client" => Client
    case "workshop-manager" => WorkshopManager
    case "warehouse-manager" => WarehouseManager
    case name => throw new IllegalArgumentException("Bad UserRole Name: " + name)
  }
}

trait Identifiable {def id: String}

case class User(id: String,
                name: String,
                username: String,
                email: String,
                phone: Option[String],
                address: Option[String],
                role: UserRole
               ) extends Identifiable


class UserRepo {
  private val base_user = User("user-id-for-client", "John Doe", "john_doe", "johndoe@example.com",
    Some("555-5555"), Some("Your house"), UserRole.Client)

  private val Users = List(
    base_user.copy(id = "user-id-for-admin", role = UserRole.Admin),
    base_user,
    base_user.copy(id = "user-id-for-workshop-manager", role = UserRole.WorkshopManager),
    base_user.copy(id = "user-id-for-warehouse-manager", role = UserRole.WarehouseManager)
  )

  def user(id: String): Option[User] = Users find (_.id == id)
  def users: List[User] = Users
}
