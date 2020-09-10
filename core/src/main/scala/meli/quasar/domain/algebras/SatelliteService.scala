package meli.quasar.domain.algebras

import meli.quasar.domain.{Distance, Message, Name, Point, SatelliteRepr}

trait SatelliteService[F[_]] {
  def getLocation(satellites: List[(Name, Distance)]): F[Point]
  def getMessage(messages: List[Message]): F[Message]
  def repr(satelliteRepr: SatelliteRepr): F[Unit]
  def getRepr: F[Map[Name, SatelliteRepr]]
}

object SatelliteService {
  def apply[F[_]: SatelliteService]: SatelliteService[F] = implicitly
}
