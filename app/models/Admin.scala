package models

import org.joda.time._

import ybr.playground.models.Nameable

case class Admin(
  id: Id,
  firstName: String,
  lastName: String,
  email: String,
  active: Boolean,
  creation: DateTime
) extends Identifiable with Nameable
