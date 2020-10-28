package graphql.auth

import models.User
import sangria.execution.FieldTag

object Tags {
  sealed trait UserTag extends FieldTag {
    def check(u: Option[User]): Boolean = u.nonEmpty
  }
  sealed trait ActiveTag extends UserTag {
    override def check(u: Option[User]): Boolean = super.check(u.filter(_.status == User.Status.Active))
  }
  sealed trait InactiveTag extends UserTag {
    override def check(u: Option[User]): Boolean = super.check(u.filter(_.status != User.Status.Active))
  }
  sealed trait RoleTag extends UserTag {
    def fun: User => Boolean
    override def check(u: Option[User]): Boolean = super.check(u.filter(fun))
  }

  case object NotAuthenticatedTag extends UserTag {override def check(u: Option[User]) = u.isEmpty}
  case object AuthenticatedTag extends UserTag
  case object ActiveTag extends UserTag with ActiveTag
  case object ClientTag extends RoleTag with ActiveTag {val fun = _.isClient}
  case object FairyTag extends RoleTag with ActiveTag {val fun = _.isFairy}
  case object AdminTag extends RoleTag with ActiveTag {val fun = _.isAdmin}

  case object WorkerTag extends RoleTag with ActiveTag { val fun = _.isWorker}
  case object WorkshopTag extends RoleTag with ActiveTag { val fun = _.isWorkshop}
  case object WorkshopWorkerTag extends RoleTag with ActiveTag {val fun = _.isWorkshopWorker}
  case object WorkshopManagerTag extends RoleTag with ActiveTag {val fun = _.isWorkshopManager}
  case object WarehouseTag extends RoleTag with ActiveTag { val fun = _.isWarehouse}
  case object WarehouseManagerTag extends RoleTag with ActiveTag {val fun = _.isWarehouseManager}
  case object WarehouseWorkerTag extends RoleTag with ActiveTag {val fun = _.isWarehouseWorker}
  case object ManagerTag extends RoleTag with ActiveTag {val fun = _.isManager}
}
