package meli.quasar.program.interpreters

import cats.effect.Async
import cats.syntax.flatMap._
import cats.syntax.functor._
import meli.quasar.domain.algebras.SatellitesRepository
import meli.quasar.domain.{Name, Satellite}
import meli.quasar.ops.morpher._
import meli.quasar.repository.reference.Transactor
import meli.quasar.repository.rows.SatelliteRow

final class MemSatellitesRepository[F[_]: Async] private (
  private val xa: Transactor[F, Map[String, SatelliteRow]]
) extends SatellitesRepository[F, Satellite] {
  override def store(satellite: Satellite): F[Unit] =
    for {
      storage <- xa.ref.get
      row = satellite.name.value -> satellite.to[SatelliteRow]
      next <- xa.ref.set(storage + row)
    } yield next

  override def fetch: F[Map[Name, Satellite]] = xa.ref.get.map(_.map { case (k, v) => Name(k) -> v.to[Satellite] })
}

object MemSatellitesRepository {
  def apply[F[_]: Async](xa: Transactor[F, Map[String, SatelliteRow]]): MemSatellitesRepository[F] =
    new MemSatellitesRepository(xa)
}
