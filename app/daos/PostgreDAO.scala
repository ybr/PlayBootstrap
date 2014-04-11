package daos

import scala.concurrent.ExecutionContext

import play.api.Play._
import play.api.libs.concurrent.Akka

import models._

trait PostgreDAO extends DAO {}
