package app.models

import java.io.Serializable

import org.mindrot.jbcrypt.BCrypt

object UserRole extends Enumeration {
  protected case class Val(override val id: Int, name: String)
    extends super.Val(id)
      with Identifiable[Int] {
  }

  def apply(s: String): UserRole = withName(s)

  import scala.language.implicitConversions
  implicit def fromId(id: Int): UserRole = apply(id);
  implicit def fromString(s: String): UserRole = apply(s)

  val Admin = Value(0, "admin")
  val Fairy = Value(1, "fairy")
  val Client = Value(2, "client")
  val WorkshopManager = Value(3, "workshop-manager")
  val WarehouseManager = Value(4, "warehouse-manager")
}

case class User(id: String,
                password: String,
                name: String,
                email: String,
                phone: Option[String],
                address: Option[String],
                role: UserRole = UserRole.Client
               ) extends Identifiable[String] with Serializable {
  def apply(id: String, password: String,
            name: String, email: String, phone: Option[String], address: Option[String],
            role: UserRole) =
    new User(id, User.hashpw(password), name, email, phone, address, role)
  def checkpw(s: String): Boolean = User.checkpw(s, password)
}

object User extends {
  private val salt = BCrypt.gensalt(12)
  def hashpw(s: String): String = BCrypt.hashpw(s, salt)
  def checkpw(s: String, pw: String): Boolean = BCrypt.checkpw(s, pw)
  val tupled = (User.apply _).tupled
  case class Credentials(id: String, password: String)
}
