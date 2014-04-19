package models

import play.api.libs.json._

trait Id {
  def value: String
  override def toString = s"Id(${value})"
}

trait IdProvider[T] {
  def toId(t: T): Id
}

object Id {
  def apply[T](t: T)(implicit provider: IdProvider[T]) = provider toId t
}
