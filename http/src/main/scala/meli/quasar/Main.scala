package meli.quasar

import cats.effect._
import cats.effect.concurrent.Ref
import meli.quasar.handlers.AppErrorHandler
import meli.quasar.program.modules.{InMemoryRepository, LiveService, LiveValidator}
import meli.quasar.repository.reference.InMemoryTransactor
import meli.quasar.repository.rows.{SatelliteReprRow, SatelliteRow}
import meli.quasar.routes.LiveHttpApi
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.AutoSlash
import io.circe.config.parser
import scala.concurrent.ExecutionContext

object Main extends IOApp {
  override def run(args: List[String]): IO[ExitCode] =
    Resource.liftF(parser.decodePathF[IO, Int]("http.port")).use { port =>
      InMemoryApp.make[IO].use { service =>
        val api = LiveHttpApi[IO](service, AppErrorHandler[IO])
        val httpApp = AutoSlash(api.routes)

        BlazeServerBuilder[IO](ExecutionContext.global)
          .bindHttp(port, "localhost")
          .withHttpApp(httpApp.orNotFound)
          .serve
          .compile
          .drain
      }
    } as ExitCode.Success
}

object InMemoryApp {
  def make[F[_]: ContextShift: ConcurrentEffect: Timer]: Resource[F, LiveService[F]] = {
    val satelliteStorage = Map(
      "kenobi" -> SatelliteRow("kenobi", -500d, -200d, Nil),
      "skywalker" -> SatelliteRow("skywalker", 100d, -100d, Nil),
      "sato" -> SatelliteRow("sato", 500d, 100d, Nil)
    )
    val satelliteReprStorage = Map.empty[String, SatelliteReprRow]
    for {
      satelliteRef  <- Resource.liftF(Ref.of(satelliteStorage))
      satelliteRepr <- Resource.liftF(Ref.of(satelliteReprStorage))
    } yield {
      val tx = InMemoryTransactor[F, Map[String, SatelliteRow]](satelliteRef, satelliteStorage)
      val txr = InMemoryTransactor[F, Map[String, SatelliteReprRow]](satelliteRepr, satelliteReprStorage)
      val repository = InMemoryRepository[F](tx, txr)
      val validator = LiveValidator[F](repository)
      LiveService[F](repository, validator)
    }
  }
}
