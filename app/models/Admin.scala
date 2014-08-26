package models

import org.joda.time._

import playground.models._

case class Admin(
  id: Id,
  firstName: String,
  lastName: String,
  email: String,
  active: Boolean,
  creation: DateTime
) extends Identifiable with Nameable
