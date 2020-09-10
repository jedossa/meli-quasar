package meli.quasar.domain.algebras

import meli.quasar.domain.{Distance, Name, Satellite}

trait SatelliteValidator[F[_]] {
  def initialized: F[Unit]
  def solvable(satellites: List[(Name, Distance)]): F[Map[Name, Satellite]]
}

object SatelliteValidator {
  def apply[F[_]: SatelliteValidator]: SatelliteValidator[F] =
    implicitly
}
