/*
 * Copyright (c) S4N 2019
 */

package meli.quasar.routes

import cats.effect.Async
import cats.syntax.semigroupk._
import meli.quasar.domain.algebras.SatelliteService
import meli.quasar.handlers.HttpErrorHandler
import meli.quasar.program.modules.Service
import org.http4s.HttpRoutes
import org.http4s.server.Router

trait HttpApi[F[_]] {
  def routes: HttpRoutes[F]
}

final class LiveHttpApi[F[_]] private (private val appRoutes: HttpRoutes[F]) extends HttpApi[F] {
  override def routes: HttpRoutes[F] = appRoutes
}

object LiveHttpApi {
  def apply[F[_]: Async](service: Service[F], appErrorHandler: HttpErrorHandler[F, Throwable]): LiveHttpApi[F] = {
    implicit val satelliteService: SatelliteService[F] = service.satelliteService
    implicit val appHandler: HttpErrorHandler[F, Throwable] = appErrorHandler
    new LiveHttpApi[F](
      Router("/topsecret" -> LiveSatelliteRoutes[F].routes) <+>
        Router("/topsecret_split" -> LiveSatelliteSplitRoutes[F].routes)
    )
  }
}
