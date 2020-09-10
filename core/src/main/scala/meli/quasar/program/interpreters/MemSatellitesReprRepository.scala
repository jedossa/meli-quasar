package meli.quasar.program.interpreters

import cats.effect.Async
import cats.syntax.flatMap._
import cats.syntax.functor._
import meli.quasar.domain.algebras.SatellitesRepository
import meli.quasar.domain.{Name, SatelliteRepr}
import meli.quasar.ops.morpher._
import meli.quasar.repository.reference.Transactor
import meli.quasar.repository.rows.SatelliteReprRow

final class MemSatellitesReprRepository[F[_]: Async] private (
  private val xa: Transactor[F, Map[String, SatelliteReprRow]]
) extends SatellitesRepository[F, SatelliteRepr] {
  override def store(satellite: SatelliteRepr): F[Unit] =
    for {
      storage <- xa.ref.get
      row = satellite.name.value -> satellite.to[SatelliteReprRow]
      next <- xa.ref.set(storage + row)
    } yield next

  override def fetch: F[Map[Name, SatelliteRepr]] =
    xa.ref.get.map(_.map { case (k, v) => Name(k) -> v.to[SatelliteRepr] })
}

object MemSatellitesReprRepository {
  def apply[F[_]: Async](xa: Transactor[F, Map[String, SatelliteReprRow]]): MemSatellitesReprRepository[F] =
    new MemSatellitesReprRepository(xa)
}
