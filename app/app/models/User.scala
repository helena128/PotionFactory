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
  val Client = Value(1, "client")
  val Fairy = Value(10, "fairy")
  val WorkshopManager = Value(1000, "workshop-manager")
  val WorkshopWorker = Value(1001, "workshop-worker")
  val WarehouseManager = Value(2000, "warehouse-manager")
  val WarehouseWorker = Value(2001, "warehouse-worker")

  def isAdmin(u: UserRole): Boolean = Seq(Admin, Fairy).contains(u)
  def isWorker(u: UserRole): Boolean = Seq(WorkshopWorker, WarehouseWorker).contains(u)
  def isWorkshop(u: UserRole): Boolean = Seq(WorkshopWorker, WorkshopManager).contains(u)
  def isWarehouse(u: UserRole): Boolean = Seq(WarehouseWorker, WarehouseManager).contains(u)
  def isManager(u: UserRole): Boolean = Seq(WorkshopManager, WarehouseManager).contains(u)
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

  def isClient: Boolean = role == UserRole.Client
  def isFairy: Boolean = role == UserRole.Fairy
  def isWorkshopManager: Boolean = role == UserRole.WorkshopManager
  def isWorkshopWorker: Boolean = role == UserRole.WorkshopWorker
  def isWarehouseManager: Boolean = role == UserRole.WarehouseManager
  def isWarehouseWorker: Boolean = role == UserRole.WarehouseWorker

  def isAdmin: Boolean = UserRole.isAdmin(role)
  def isWorker: Boolean = UserRole.isWorker(role)
  def isWorkshop: Boolean = UserRole.isWorkshop(role)
  def isWarehouse: Boolean = UserRole.isWarehouse(role)
  def isManager: Boolean = UserRole.isManager(role)
}

object User extends {
  private val salt = BCrypt.gensalt(12)
  def hashpw(s: String): String = BCrypt.hashpw(s, salt)
  def checkpw(s: String, pw: String): Boolean = BCrypt.checkpw(s, pw)
  val tupled = (User.apply _).tupled
  case class Credentials(id: String, password: String)
}
