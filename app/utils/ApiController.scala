package utils

import scala.concurrent.Future

import play.api.mvc._

import org.apache.commons.codec.binary.Base64

import ybr.playground.conf._

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

  def basicAuth(request: RequestHeader): Option[(String, Password)] = request.headers.get("Authorization") flatMap { authorization =>
    val basic = "Basic (.*)".r
    authorization match {
      case basic(base64) => new String(Base64.decodeBase64(base64), "UTF-8").split(":").toList match {
        case login::password::_ => Some(login -> Password(password))
        case _ => None
      }
      case _ => None
    }
  }
}
