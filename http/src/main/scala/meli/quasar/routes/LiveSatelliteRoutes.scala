/*
 * Copyright (c) S4N 2019
 */

package meli.quasar.routes

import cats.effect.Async
import cats.syntax.flatMap._
import cats.syntax.functor._
import io.circe.generic.auto._
import meli.quasar.domain.algebras.SatelliteService
import meli.quasar.dtos.{TopResponseDTO, TopSecretDTO}
import meli.quasar.handlers.HttpErrorHandler
import meli.quasar.ops.morpher._
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityCodec.{circeEntityDecoder, circeEntityEncoder}

final class LiveSatelliteRoutes[F[_]: SatelliteService: Async] private (
  implicit private val appErrorHandler: HttpErrorHandler[F, Throwable]
) extends RoutesF[F](appErrorHandler) {
  override protected def endpoints: HttpRoutes[F] =
    HttpRoutes.of[F] {
      case request @ POST -> Root =>
        request.decode[TopSecretDTO] { secret =>
          val response = for {
            location <- SatelliteService[F].getLocation(secret.to)
            message  <- SatelliteService[F].getMessage(secret.to)
          } yield (location, message).to[TopResponseDTO]
          Ok(response)
        }
    }
}

object LiveSatelliteRoutes {
  def apply[F[_]: SatelliteService: Async: HttpErrorHandler[*[_], Throwable]]: RoutesF[F] = new LiveSatelliteRoutes[F]
}
