package models.requests

import models.Id

trait RequestCreate[T] {
  def withId(id: Id): T
}

trait RequestUpdate[T, R] {
  def from(t: T): R
}
