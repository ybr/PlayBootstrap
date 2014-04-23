package models.requests

import org.joda.time._

import models._

case class AdminCreate(
  firstName: String,
  lastName: String,
  email: String,
  creation: DateTime
) extends RequestCreate[Admin] {
  def withId(id: Id): Admin = Admin(id, firstName, lastName, email, creation)
}
