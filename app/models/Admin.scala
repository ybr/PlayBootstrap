package models

import org.joda.time._

case class Admin(
  id: Id,
  firstName: String,
  lastName: String,
  email: String,
  creation: DateTime
) extends Identifiable
