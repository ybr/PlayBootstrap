package models

import org.joda.time._

case class User(
  id: Id,
  firstName: String,
  lastName: String,
  email: String,
  creation: DateTime
) extends Identifiable