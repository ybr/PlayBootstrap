package utils

import scala.concurrent.Future

import play.api.mvc._

import playground.conf._
import playground.utils.Authorization._

import models._

object API extends ActionBuilder[Request] {
  private val login = Conf("api.login")
  private val password = Conf("api.password")

  def invokeBlock[A](request: Request[A], block: Request[A] => Future[SimpleResult]) = block(request)

  override def composeAction[A](action: Action[A]) = new API(action)
}

case class API[A](action: Action[A]) extends Action[A] with Results {
  def apply(request: Request[A]): Future[SimpleResult] = basicAuth(request) match {
    case Some(_) => action(request)
    case None => Future.successful(Unauthorized)
  }

  lazy val parser = action.parser
}
