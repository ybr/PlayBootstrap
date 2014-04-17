package models

import play.api.libs.json._

trait Id {
  def value: Long
}

trait IdProvider[T] {
  def toId(t: T): Id
}

object Id {
  def apply[T](t: T)(implicit provider: IdProvider[T]) = provider toId t
}
