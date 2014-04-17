package daos

import models._

trait PostgreDAO extends DAO {
  val unique_violation = "23505"

  implicit val postgreIdProvider = new IdProvider[Long] {
    def toId(l: Long) = new Id {
      val value = l
    }
  }
}
