package utils

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits._

object FutureUtils {
  def sequence[A](in: Option[Future[A]]): Future[Option[A]] = in match {
    case Some(f) => f.map(Some(_))
    case None => Future.successful(None)
  }
}
