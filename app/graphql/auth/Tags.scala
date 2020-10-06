package graphql.auth

import models.User
import sangria.execution.FieldTag
import security.AppContext

object Tags {
  sealed trait RoleTag extends FieldTag {
    def check(u: Option[User]): Boolean
    def check(u: Option[User], _ctx: AppContext): Boolean = check(u)
  }

  case object AuthenticatedTag extends RoleTag {def check(u: Option[User]) = u.nonEmpty}
  case object NotAuthenticatedTag extends RoleTag {def check(u: Option[User]) = u.isEmpty}
  case object ClientTag extends RoleTag {def check(u: Option[User]) = u.exists(_.isClient)}
  case object FairyTag extends RoleTag {def check(u: Option[User]) = u.exists(_.isFairy)}
  case object AdminTag extends RoleTag {def check(u: Option[User]) = u.exists(_.isAdmin)}

  case object WorkerTag extends RoleTag { def check(u: Option[User]) = u.exists(_.isWorker)}
  case object WorkshopTag extends RoleTag { def check(u: Option[User]) = u.exists(_.isWorkshop) }
  case object WorkshopWorkerTag extends RoleTag {def check(u: Option[User]) = u.exists(_.isWorkshopWorker)}
  case object WorkshopManagerTag extends RoleTag {def check(u: Option[User]) = u.exists(_.isWorkshopManager)}
  case object WarehouseTag extends RoleTag { def check(u: Option[User]) = u.exists(_.isWarehouse) }
  case object WarehouseManagerTag extends RoleTag {def check(u: Option[User]) = u.exists(_.isWarehouseManager)}
  case object WarehouseWorkerTag extends RoleTag {def check(u: Option[User]) = u.exists(_.isWarehouseWorker)}
  case object ManagerTag extends RoleTag {def check(u: Option[User]) = u.exists(_.isManager)}
}
