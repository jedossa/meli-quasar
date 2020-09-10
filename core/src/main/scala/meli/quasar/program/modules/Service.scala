package meli.quasar.program.modules

import cats.effect.Async
import meli.quasar.domain.algebras.{SatelliteService, SatelliteValidator, SatellitesRepository}
import meli.quasar.domain.{Satellite, SatelliteRepr}
import meli.quasar.program.interpreters.LiveSatelliteService

trait Service[F[_]] {
  def satelliteService: SatelliteService[F]
}

final class LiveService[F[_]] private (private val satellites: SatelliteService[F]) extends Service[F] {
  override def satelliteService: SatelliteService[F] = satellites
}

object LiveService {
  def apply[F[_]: Async](
    repository: Repository[F],
    Validator: Validator[F]
  ): LiveService[F] = {
    implicit val satelliteRepository: SatellitesRepository[F, Satellite] = repository.satellitesRepository
    implicit val satelliteReprRepository: SatellitesRepository[F, SatelliteRepr] = repository.satellitesReprRepository
    implicit val satelliteValidator: SatelliteValidator[F] = Validator.satelliteValidator
    val satelliteService: SatelliteService[F] = LiveSatelliteService[F]
    new LiveService(satelliteService)
  }
}
