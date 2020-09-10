/*
 * Copyright (c) S4N 2019
 */

package meli.quasar.routes

import cats.effect.Async
import cats.syntax.apply._
import cats.syntax.flatMap._
import cats.syntax.functor._
import io.circe.generic.auto._
import meli.quasar.domain.algebras.SatelliteService
import meli.quasar.dtos.{SatelliteDTO, TopResponseDTO, TopSecretDTO, TopSecretSplitDTO}
import meli.quasar.handlers.HttpErrorHandler
import meli.quasar.ops.morpher._
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityCodec.{circeEntityDecoder, circeEntityEncoder}

final class LiveSatelliteSplitRoutes[F[_]: SatelliteService: Async] private (
  implicit private val appErrorHandler: HttpErrorHandler[F, Throwable]
) extends RoutesF[F](appErrorHandler) {
  override protected def endpoints: HttpRoutes[F] =
    HttpRoutes.of[F] {
      case request @ POST -> Root / name =>
        request.decode[TopSecretSplitDTO] { secret =>
          SatelliteService[F].repr((name, secret).to) *> Accepted()
        }
      case GET -> Root =>
        SatelliteService[F].getRepr.flatMap { secrets =>
          val secret = TopSecretDTO(secrets.values.map(_.to[SatelliteDTO]).toList)
          val response = for {
            location <- SatelliteService[F].getLocation(secret.to)
            message  <- SatelliteService[F].getMessage(secret.to)
          } yield (location, message).to[TopResponseDTO]
          Ok(response)
        }
    }
}

object LiveSatelliteSplitRoutes {
  def apply[F[_]: SatelliteService: Async: HttpErrorHandler[*[_], Throwable]]: RoutesF[F] =
    new LiveSatelliteSplitRoutes[F]
}
