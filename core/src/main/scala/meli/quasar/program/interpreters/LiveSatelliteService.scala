package meli.quasar.program.interpreters

import cats.effect.Async
import cats.instances.string._
import cats.syntax.applicative._
import cats.syntax.applicativeError._
import cats.syntax.apply._
import cats.syntax.eq._
import cats.syntax.flatMap._
import meli.quasar.domain._
import meli.quasar.domain.algebras.{SatelliteService, SatelliteValidator, SatellitesRepository}

final class LiveSatelliteService[
  F[_]: Async: SatellitesRepository[*[_], Satellite]: SatellitesRepository[*[_], SatelliteRepr]: SatelliteValidator] private
  extends SatelliteService[F] {
  override def getLocation(satellites: List[(Name, Distance)]): F[Point] =
    SatelliteValidator[F].initialized *> SatelliteValidator[F].solvable(satellites) flatMap { storage =>
      satellites.sortBy(_._2.value) match {
        case (n0, Distance(d0)) :: (n1, Distance(d1)) :: (n3, d3) :: _ =>
          val solutions = for {
            p0 <- storage.get(n0)
            p1 <- storage.get(n1)
            p2 <- storage.get(n3)
            d = Point.distance(p1.location, p0.location).value
            d01 = (Math.pow(d0, 2) - Math.pow(d1, 2) + Math.pow(d, 2)) / (2 * d)
            h = Math.sqrt(Math.pow(d0, 2) - Math.pow(d01, 2))
          } yield {
            val p2x = p0.location.x + d01 * (p1.location.x - p0.location.x) / d
            val p2y = p0.location.y + d01 * (p1.location.y - p0.location.y) / d
            val x3p = p2x + h * (p1.location.y - p0.location.y) / d
            val x3n = p2x - h * (p1.location.y - p0.location.y) / d
            val y3n = p2y - h * (p1.location.x - p0.location.x) / d
            val y3p = p2y + h * (p1.location.x - p0.location.x) / d

            (Point(x3p, y3n), Point(x3n, y3p), p2.location)
          }
          solutions.fold[F[Point]](InsufficientInformation().raiseError) {
            case (sln1, sln2, p2) =>
              if (Point.distance(sln1, p2) === d3) sln1.pure
              else if (Point.distance(sln2, p2) === d3) sln2.pure
              else InsufficientInformation().raiseError
          }
        case _ => InsufficientInformation().raiseError
      }
    }

  override def getMessage(messages: List[Message]): F[Message] =
    messages
      .map(_.words)
      .reduceOption(_.zipAll(_, "", "").map {
        case (a, b) if a === b => a
        case (a, b) => s"$a $b".trim
      })
      .fold[F[Message]](NoMessage().raiseError)(Message(_).pure)

  override def repr(satelliteRepr: SatelliteRepr): F[Unit] = SatellitesRepository[F, SatelliteRepr].store(satelliteRepr)

  override def getRepr: F[Map[Name, SatelliteRepr]] = SatellitesRepository[F, SatelliteRepr].fetch
}

object LiveSatelliteService {
  def apply[
    F[_]: Async: SatellitesRepository[*[_], Satellite]: SatellitesRepository[*[_], SatelliteRepr]: SatelliteValidator]
    : SatelliteService[F] =
    new LiveSatelliteService[F]
}
