package meli.quasar.program.interpreters

import cats.effect.IO
import cats.effect.concurrent.Ref
import meli.quasar.domain.algebras.{SatelliteValidator, SatellitesRepository}
import meli.quasar.domain._
import meli.quasar.generators.BaseGenerator
import meli.quasar.ops.morpher._
import meli.quasar.repository.reference.InMemoryTransactor
import meli.quasar.repository.rows.{SatelliteReprRow, SatelliteRow}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks

class LiveSatelliteServiceTest extends AnyFunSuite with ScalaCheckDrivenPropertyChecks with BaseGenerator {
  test("Should get original location") {
    val satelliteStorage: Map[String, SatelliteRow] = Map(
      "kenobi" -> kenobi.to[SatelliteRow],
      "skywalker" -> skywalker.to[SatelliteRow],
      "sato" -> sato.to[SatelliteRow]
    )
    val satelliteReprStorage: Map[String, SatelliteReprRow] = Map.empty
    val program = for {
      xa <- Ref.of[IO, Map[String, SatelliteRow]](satelliteStorage).map(InMemoryTransactor(_, satelliteStorage))
      xr <- Ref
        .of[IO, Map[String, SatelliteReprRow]](satelliteReprStorage)
        .map(InMemoryTransactor(_, satelliteReprStorage))
      implicit0(repository: SatellitesRepository[IO, Satellite]) = MemSatellitesRepository[IO](xa)
      implicit0(repr: SatellitesRepository[IO, SatelliteRepr]) = MemSatellitesReprRepository[IO](xr)
      implicit0(validator: SatelliteValidator[IO]) = LiveSatelliteValidator[IO]
      location <- LiveSatelliteService[IO].getLocation(
        List(
          Name("kenobi") -> Distance(921.9544457292888),
          Name("skywalker") -> Distance(509.9019513592785),
          Name("sato") -> Distance(424.2640687119285)
        )
      )
    } yield location.x === 200d && location.y === 400
    assert(program.unsafeRunSync())
  }

  test("Should get original message") {
    val satelliteStorage: Map[String, SatelliteRow] = Map(
      "kenobi" -> kenobi.to[SatelliteRow],
      "skywalker" -> skywalker.to[SatelliteRow],
      "sato" -> sato.to[SatelliteRow]
    )
    val satelliteReprStorage: Map[String, SatelliteReprRow] = Map.empty
    val program = for {
      xa <- Ref.of[IO, Map[String, SatelliteRow]](satelliteStorage).map(InMemoryTransactor(_, satelliteStorage))
      xr <- Ref
        .of[IO, Map[String, SatelliteReprRow]](satelliteReprStorage)
        .map(InMemoryTransactor(_, satelliteReprStorage))
      implicit0(repository: SatellitesRepository[IO, Satellite]) = MemSatellitesRepository[IO](xa)
      implicit0(repr: SatellitesRepository[IO, SatelliteRepr]) = MemSatellitesReprRepository[IO](xr)
      implicit0(validator: SatelliteValidator[IO]) = LiveSatelliteValidator[IO]
      message <- LiveSatelliteService[IO].getMessage(
        List(
          Message(List("", "es", "", "mensaje", "secreto")),
          Message(List("este", "", "un", "mensaje", "")),
          Message(List("este", "", "", "mensaje", ""))
        )
      )
    } yield message.words.mkString(" ") === "este es un mensaje secreto"
    assert(program.unsafeRunSync())
  }

  test("Should fail with insufficient information") {
    val satelliteStorage: Map[String, SatelliteRow] = Map(
      "kenobi" -> kenobi.to[SatelliteRow],
      "skywalker" -> skywalker.to[SatelliteRow],
      "sato" -> sato.to[SatelliteRow]
    )
    val satelliteReprStorage: Map[String, SatelliteReprRow] = Map.empty
    val program = for {
      xa <- Ref.of[IO, Map[String, SatelliteRow]](satelliteStorage).map(InMemoryTransactor(_, satelliteStorage))
      xr <- Ref
        .of[IO, Map[String, SatelliteReprRow]](satelliteReprStorage)
        .map(InMemoryTransactor(_, satelliteReprStorage))
      implicit0(repository: SatellitesRepository[IO, Satellite]) = MemSatellitesRepository[IO](xa)
      implicit0(repr: SatellitesRepository[IO, SatelliteRepr]) = MemSatellitesReprRepository[IO](xr)
      implicit0(validator: SatelliteValidator[IO]) = LiveSatelliteValidator[IO]
      _ <- LiveSatelliteService[IO].getLocation(
        List(
          Name("kenobi") -> Distance(921.9544457292888),
          Name("skywalker") -> Distance(509.9019513592785),
          Name("sato") -> Distance(1424.2640687119285)
        )
      )
    } yield ()
    assert(
      program.attempt.map(_.swap.map(_.getMessage).getOrElse("")).unsafeRunSync() === "No hay suficiente informacion")
  }
}
