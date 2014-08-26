package daos

import java.util.Date

import org.joda.time.DateTime

import playground.models._

trait PostgreDAO extends DAO {
  val unique_violation = "23505"

  def param(id: Id): Long = id.value.toLong
  def param(d: DateTime): Date = d.toDate

  implicit val postgreIdProvider = new IdProvider[Long] {
    def toId(l: Long) = new Id {
      val value = l.toString
    }
  }
}
