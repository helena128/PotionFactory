package graphql.user

import graphql.Types._
import models.User
import models.User._
import sangria.macros.derive._
import security.AppContext

object Types {
  implicit val UserRoleType = deriveEnumType[Role.Value](EnumTypeName("UserRole"),
    IncludeValues("Admin", "Fairy", "Client", "WorkshopManager", "WorkshopWorker", "WarehouseManager", "WarehouseWorker"))

  implicit val UserStatusType = deriveEnumType[Status.Value](EnumTypeName("UserStatus"),
    IncludeValues("Verification", "Active", "Deactivated"))

  implicit val UserType = deriveObjectType[AppContext, User](
    Interfaces(IdentifiableWithStringType),
    ObjectTypeName("User"),
    ObjectTypeDescription("User account and info"),
    ExcludeFields("password"))
}
