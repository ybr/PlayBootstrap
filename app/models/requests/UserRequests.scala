package models.requests

import org.joda.time._

import playground.models._

import models._

case class UserCreate(
  firstName: String,
  lastName: String,
  email: String,
  active: Boolean,
  creation: DateTime
) extends RequestCreate[User] {
  def withId(id: Id): User = User(id, firstName, lastName, email, active, creation)
}

case class UserUpdate(
  firstName: String,
  lastName: String,
  email: String,
  active: Boolean
)

object UserUpdate extends RequestUpdate[User, UserUpdate] {
  def from(u: User) = UserUpdate(u.firstName, u.lastName, u.email, u.active)
}
