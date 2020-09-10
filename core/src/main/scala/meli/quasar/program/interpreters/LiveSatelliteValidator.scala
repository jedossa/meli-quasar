package meli.quasar.program.interpreters

import cats.effect.Async
import cats.instances.double._
import cats.syntax.applicative._
import cats.syntax.applicativeError._
import cats.syntax.eq._
import cats.syntax.flatMap._
import meli.quasar.domain._
import meli.quasar.domain.algebras.{SatelliteValidator, SatellitesRepository}
import mouse.boolean._

final class LiveSatelliteValidator[F[_]: Async: SatellitesRepository[*[_], Satellite]] private
  extends SatelliteValidator[F] {
  override def initialized: F[Unit] =
    SatellitesRepository[F, Satellite].fetch.flatMap(_.nonEmpty.fold(().pure, NoSatellites().raiseError))

  override def solvable(satellites: List[(Name, Distance)]): F[Map[Name, Satellite]] =
    SatellitesRepository[F, Satellite].fetch.flatMap { map =>
      val error: F[Map[Name, Satellite]] = InsufficientInformation().raiseError
      satellites.sortBy(_._2.value) match {
        case (n0, Distance(d0)) :: (n1, Distance(d1)) :: _ =>
          val distance = for {
            p0 <- map.get(n0)
            p1 <- map.get(n1)
          } yield Point.distance(p1.location, p0.location)

          distance.fold(error) {
            case Distance(d) =>
              if (d > d0 + d1) error
              else if (d < Math.abs(d1 - d0)) error
              else if (d === 0.0d && d0 === d1) error
              else map.pure
          }
        case _ => error
      }
    }
}

object LiveSatelliteValidator {
  def apply[F[_]: Async: SatellitesRepository[*[_], Satellite]]: SatelliteValidator[F] =
    new LiveSatelliteValidator[F]
}
