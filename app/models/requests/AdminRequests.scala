package models.requests

import org.joda.time._

import playground.models._

import models._

case class AdminCreate(
  firstName: String,
  lastName: String,
  email: String,
  active: Boolean,
  creation: DateTime
) extends RequestCreate[Admin] {
  def withId(id: Id): Admin = Admin(id, firstName, lastName, email, active, creation)
}

case class AdminUpdate(
  firstName: String,
  lastName: String,
  email: String,
  active: Boolean
)

object AdminUpdate extends RequestUpdate[Admin, AdminUpdate] {
  def from(a: Admin) = AdminUpdate(a.firstName, a.lastName, a.email, a.active)
}
