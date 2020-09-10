package meli.quasar.program.modules

import cats.effect.Async
import meli.quasar.domain.algebras.SatellitesRepository
import meli.quasar.domain.{Satellite, SatelliteRepr}
import meli.quasar.program.interpreters.{MemSatellitesRepository, MemSatellitesReprRepository}
import meli.quasar.repository.reference.Transactor
import meli.quasar.repository.rows.{SatelliteReprRow, SatelliteRow}

trait Repository[F[_]] {
  def satellitesRepository: SatellitesRepository[F, Satellite]
  def satellitesReprRepository: SatellitesRepository[F, SatelliteRepr]
}

final class InMemoryRepository[F[_]] private (
  private val satelliteRepo: SatellitesRepository[F, Satellite],
  private val satelliteRepr: SatellitesRepository[F, SatelliteRepr])
  extends Repository[F] {
  override def satellitesRepository: SatellitesRepository[F, Satellite] = satelliteRepo
  override def satellitesReprRepository: SatellitesRepository[F, SatelliteRepr] = satelliteRepr
}

object InMemoryRepository {
  def apply[F[_]: Async](
    tx: Transactor[F, Map[String, SatelliteRow]],
    txr: Transactor[F, Map[String, SatelliteReprRow]]): InMemoryRepository[F] =
    new InMemoryRepository[F](MemSatellitesRepository[F](tx), MemSatellitesReprRepository[F](txr))
}
