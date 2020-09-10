package meli.quasar.program.modules

import cats.effect.Async
import meli.quasar.domain.Satellite
import meli.quasar.domain.algebras.{SatelliteValidator, SatellitesRepository}
import meli.quasar.program.interpreters.LiveSatelliteValidator

trait Validator[F[_]] {
  def satelliteValidator: SatelliteValidator[F]
}

final class LiveValidator[F[_]] private (private val satelliteV: SatelliteValidator[F]) extends Validator[F] {
  override def satelliteValidator: SatelliteValidator[F] = satelliteV
}

object LiveValidator {
  def apply[F[_]: Async](repository: Repository[F]): LiveValidator[F] = {
    implicit val satelliteRepository: SatellitesRepository[F, Satellite] = repository.satellitesRepository
    new LiveValidator[F](LiveSatelliteValidator[F])
  }
}
