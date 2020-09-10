package meli.quasar.domain.algebras

import meli.quasar.domain.Name

trait SatellitesRepository[F[_], A] {
  def store(a: A): F[Unit]
  def fetch: F[Map[Name, A]]
}

object SatellitesRepository {
  def apply[F[_]: SatellitesRepository[*[_], A], A]: SatellitesRepository[F, A] =
    implicitly
}
