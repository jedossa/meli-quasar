/*
 * Copyright (c) S4N 2019
 */

package meli.quasar.handlers

import cats.MonadError
import meli.quasar.domain.{DomainError, InsufficientInformation, NoMessage, NoSatellites}
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl

import scala.util.control.NonFatal

final class AppErrorHandler[F[_]: MonadError[*[_], Throwable]] private
  extends HttpErrorHandler[F, Throwable] with Http4sDsl[F] {
  override def handle(routes: HttpRoutes[F]): HttpRoutes[F] =
    RoutesHttpErrorHandler[F, Throwable](routes) {
      case t: InsufficientInformation => NotFound(t: DomainError)
      case t: NoSatellites => NotFound(t: DomainError)
      case t: NoMessage => NotFound(t: DomainError)
      case NonFatal(t) => InternalServerError(t.getMessage)
    }
}

object AppErrorHandler {
  def apply[F[_]: MonadError[*[_], Throwable]]: HttpErrorHandler[F, Throwable] =
    new AppErrorHandler[F]
}
