package models

import org.joda.time._

import playground.models._

case class User(
  id: Id,
  firstName: String,
  lastName: String,
  email: String,
  active: Boolean,
  creation: DateTime
) extends Identifiable with Nameable
