/*
 * Copyright (c) S4N 2019
 */

package meli.quasar.routes

import meli.quasar.handlers.HttpErrorHandler
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl

abstract class RoutesF[F[_]](
  private val appErrorHandler: HttpErrorHandler[F, Throwable]
) extends Http4sDsl[F] {
  protected def endpoints: HttpRoutes[F]
  val routes: HttpRoutes[F] = appErrorHandler.handle(endpoints)
}
