package daos

import scala.concurrent.ExecutionContext

import play.api.Play._
import play.api.libs.concurrent.Akka

trait DAO {
  implicit val postgreExecutionContext: ExecutionContext = Akka.system.dispatchers.lookup("dao-context")
}
