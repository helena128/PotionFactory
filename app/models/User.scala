package models

import java.io.Serializable

import org.mindrot.jbcrypt.BCrypt

case class User(id: String,
                password: String,
                name: String,
                email: String,
                phone: Option[String],
                address: Option[String],
                role: User.Role = User.Role.Client
               ) extends Identifiable[String] with Serializable {
  def apply(id: String, password: String,
            name: String, email: String, phone: Option[String], address: Option[String],
            role: User.Role) =
    new User(id, User.hashpw(password), name, email, phone, address, role)
  def checkpw(s: String): Boolean = User.checkpw(s, password)

  def isClient: Boolean = role == User.Role.Client
  def isFairy: Boolean = role == User.Role.Fairy
  def isWorkshopManager: Boolean = role == User.Role.WorkshopManager
  def isWorkshopWorker: Boolean = role == User.Role.WorkshopWorker
  def isWarehouseManager: Boolean = role == User.Role.WarehouseManager
  def isWarehouseWorker: Boolean = role == User.Role.WarehouseWorker

  def isAdmin: Boolean = User.Role.isAdmin(role)
  def isWorker: Boolean = User.Role.isWorker(role)
  def isWorkshop: Boolean = User.Role.isWorkshop(role)
  def isWarehouse: Boolean = User.Role.isWarehouse(role)
  def isManager: Boolean = User.Role.isManager(role)
}

object User extends {
  private val salt = BCrypt.gensalt(12)
  def hashpw(s: String): String = BCrypt.hashpw(s, salt)
  def checkpw(s: String, pw: String): Boolean = BCrypt.checkpw(s, pw)
  val tupled = (User.apply _).tupled
  case class Credentials(id: String, password: String)

  object Role extends Enumeration {
    protected case class Val(override val id: Int, name: String)
      extends super.Val(id)
        with Identifiable[Int] {
    }

    def apply(s: String): Role = withName(s)

    import scala.language.implicitConversions
    implicit def fromId(id: Int): Role = apply(id);
    implicit def fromString(s: String): Role = apply(s)

    val Admin = Value(0, "admin")
    val Client = Value(1, "client")
    val Fairy = Value(10, "fairy")
    val WorkshopManager = Value(1000, "workshop-manager")
    val WorkshopWorker = Value(1001, "workshop-worker")
    val WarehouseManager = Value(2000, "warehouse-manager")
    val WarehouseWorker = Value(2001, "warehouse-worker")

    def isAdmin(u: Role): Boolean = Seq(Admin, Fairy).contains(u)
    def isWorker(u: Role): Boolean = Seq(WorkshopWorker, WarehouseWorker).contains(u)
    def isWorkshop(u: Role): Boolean = Seq(WorkshopWorker, WorkshopManager).contains(u)
    def isWarehouse(u: Role): Boolean = Seq(WarehouseWorker, WarehouseManager).contains(u)
    def isManager(u: Role): Boolean = Seq(WorkshopManager, WarehouseManager).contains(u)
  }
  type Role = Role.Value
}
