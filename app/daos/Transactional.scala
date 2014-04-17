package daos

import scala.concurrent._
import scala.language.higherKinds
import scala.collection.generic.CanBuildFrom
import scala.util._

import java.sql.Connection

import play.api.Play.current
import play.api.db.DB
import play.api.libs.concurrent.Akka

case class Tx[+A](private val atomic: Connection => A) {
  def map[B](f: A => B): Tx[B] = Tx(f compose atomic)

  def flatMap[B](f: A => Tx[B]): Tx[B] = Tx(c => f(atomic(c)).atomic(c))

  def zip[B](tx: Tx[B]): Tx[(A, B)] = this.flatMap { a =>
    tx.map { b =>
      a -> b
    }
  }

  def mapFailure[B <: Throwable](f: PartialFunction[Throwable, B]): Tx[A] = Tx { c =>
    Try(atomic(c)) match {
      case Success(a) => a
      case Failure(t) => throw if(f.isDefinedAt(t)) f.apply(t) else t
    }
  }

  def commit(): Future[A] = Future {
    DB.withTransaction(atomic)
  }(Tx.executionContext)
}

object Tx {
  private val executionContext: ExecutionContext = Akka.system.dispatchers.lookup("transactional-context")

  def pure[A](a: => A): Tx[A] = Tx(_ => a)

  def sequence[A](in: Option[Tx[A]]): Tx[Option[A]] = in.map(_.map(Some(_))).getOrElse(pure(None))

  def sequence[A, F[A] <: TraversableOnce[A]](in: F[Tx[A]])(implicit cbf: CanBuildFrom[F[Tx[A]], A, F[A]]): Tx[F[A]] = {
    in.foldLeft(pure(cbf(in))) { (tr, ta) =>
      for {
        r <- tr
        a <- ta
      } yield r += a
    } map (_.result())
  }
}
