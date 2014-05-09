package controllers

import scala.concurrent.Future

import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits._

import models._
import services._
import utils._

class UserRequest[A](val me: User, request: Request[A]) extends WrappedRequest[A](request)
class MaybeUserRequest[A](val maybeMe: Option[User], request: Request[A]) extends WrappedRequest[A](request)

trait UserController { self: Controller =>
  def userService = UserService

  implicit def me[A](implicit request: UserRequest[A]): User = request.me
  implicit def maybeMe[A](implicit request: MaybeUserRequest[A]): Option[User] = request.maybeMe

  object WithUser extends ActionBuilder[UserRequest] {
    def invokeBlock[A](request: Request[A], block: UserRequest[A] => Future[SimpleResult]) = {
      request.session.get("login") match {
        case Some(email) => userService.byLogin(email).map(_.filter(_.active)) flatMap {
          case Some(user) => block(new UserRequest(user, request))
          case None => Future.successful(Forbidden)
        }
        case None => {
          Future.successful(Unauthorized(views.html.visitors.signin(Authentication.signinForm)(None, flash(request), lang(request))))
        }
      }
    }
  }

  object WithMaybeUser extends ActionBuilder[MaybeUserRequest] {
    def invokeBlock[A](request: Request[A], block: MaybeUserRequest[A] => Future[SimpleResult]) = for {
      maybeEmail <- Future.successful(request.session.get("login"))
      maybeUser <- FutureUtils.sequence(maybeEmail.map(email => userService.byLogin(email).map(_.filter(_.active)))).map(_.flatten)
      result <- block(new MaybeUserRequest(maybeUser, request))
    } yield result
  }
}
