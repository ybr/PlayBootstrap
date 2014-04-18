package controllers

import scala.concurrent.Future

import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits._

import models._
import daos._
import utils._

class UserRequest[A](val me: User, request: Request[A]) extends WrappedRequest[A](request)
class MaybeUserRequest[A](val maybeMe: Option[User], request: Request[A]) extends WrappedRequest[A](request)

trait UserController extends Controller {
  implicit def user[A](implicit request: UserRequest[A]): User = request.me
  implicit def maybeUser[A](implicit request: MaybeUserRequest[A]): Option[User] = request.maybeMe

  object WithUser extends ActionBuilder[UserRequest] {
    private val userDAO: UserDAO = UserPostgreDAO

    def invokeBlock[A](request: Request[A], block: UserRequest[A] => Future[SimpleResult]) = {
      request.session.get("login") match {
        case Some(email) => userDAO.byLogin(email) flatMap {
          case Some(user) => block(new UserRequest(user, request))
          case None => Future.successful(Forbidden)
        }
        case None => Future.successful(Redirect(routes.Visitors.signin))
      }
    }
  }

  object WithMaybeUser extends ActionBuilder[MaybeUserRequest] {
    private val userDAO: UserDAO = UserPostgreDAO

    def invokeBlock[A](request: Request[A], block: MaybeUserRequest[A] => Future[SimpleResult]) = for {
      maybeEmail <- Future.successful(request.session.get("login"))
      maybeUser <- FutureUtils.sequence(maybeEmail.map(email => userDAO.byLogin(email))).map(_.flatten)
      result <- block(new MaybeUserRequest(maybeUser, request))
    } yield result
  }
}
