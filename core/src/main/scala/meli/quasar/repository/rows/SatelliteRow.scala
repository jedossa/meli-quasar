package meli.quasar.repository.rows

import meli.quasar.domain.Satellite
import meli.quasar.ops.Morph

final case class SatelliteRow private (
  name: String,
  x: Double,
  y: Double,
  message: List[String]
)

object SatelliteRow {
  implicit def toDomain: Morph[SatelliteRow, Satellite] =
    row => Satellite.of(row.name, row.x, row.y, row.message)

  implicit def fromDomain: Morph[Satellite, SatelliteRow] =
    satellite =>
      new SatelliteRow(
        name = satellite.name.value,
        x = satellite.location.x,
        y = satellite.location.y,
        message = satellite.message.words)
}
