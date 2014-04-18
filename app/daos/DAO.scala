package daos

import scala.concurrent.ExecutionContext

import play.api.Play.current

import play.api.libs.concurrent.Akka

trait DAO {
  implicit val executionContext: ExecutionContext = Akka.system.dispatchers.lookup("transactional-context")
}
