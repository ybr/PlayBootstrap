package models.requests

import org.joda.time._

import models._

case class UserCreate(
  firstName: String,
  lastName: String,
  email: String,
  creation: DateTime
) extends RequestCreate[User] {
  def withId(id: Id): User = User(id, firstName, lastName, email, creation)
}

case class UserUpdate(
  firstName: String,
  lastName: String,
  email: String
)
