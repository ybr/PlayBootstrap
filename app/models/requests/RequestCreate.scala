package models.requests

import models.Id

trait RequestCreate[T] {
  def withId(id: Id): T
}
