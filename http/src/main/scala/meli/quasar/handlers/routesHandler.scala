/*
 * Copyright (c) S4N 2019
 */

package meli.quasar.handlers

import cats.ApplicativeError
import cats.data.{Kleisli, OptionT}
import cats.syntax.applicativeError._
import cats.syntax.functor._
import org.http4s.{HttpRoutes, Response}

trait HttpErrorHandler[F[_], -E] {
  def handle(routes: HttpRoutes[F]): HttpRoutes[F]
}

object HttpErrorHandler {
  def apply[F[_]: ApplicativeError[*[_], E], E](handler: E => F[Response[F]]): HttpErrorHandler[F, E] =
    RoutesHttpErrorHandler(_)(handler)
}

object RoutesHttpErrorHandler {
  def apply[F[_]: ApplicativeError[*[_], E], E](
    routes: HttpRoutes[F]
  )(handler: E => F[Response[F]]): HttpRoutes[F] =
    Kleisli { req =>
      OptionT {
        routes.run(req).value.handleErrorWith(handler(_).map(Option.apply))
      }
    }
}
