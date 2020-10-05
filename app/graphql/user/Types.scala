package graphql.user

import graphql.Types._
import models.{User, UserRole}
import sangria.macros.derive.{EnumTypeName, ExcludeFields, IncludeValues, Interfaces, ObjectTypeDescription, ObjectTypeName, deriveEnumType, deriveObjectType}
import security.AppContext

object Types {
  implicit val UserRoleType = deriveEnumType[UserRole.Value](EnumTypeName("UserRole"),
    IncludeValues("Admin", "Fairy", "Client", "WorkshopManager", "WarehouseManager"))

  implicit val UserType = deriveObjectType[AppContext, User](
    Interfaces(IdentifiableWithStringType),
    ObjectTypeName("User"),
    ObjectTypeDescription("User account and info"),
    ExcludeFields("password"))
}
