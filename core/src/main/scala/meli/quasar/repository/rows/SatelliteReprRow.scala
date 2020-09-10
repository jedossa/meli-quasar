package meli.quasar.repository.rows

import meli.quasar.domain.SatelliteRepr
import meli.quasar.ops.Morph

final case class SatelliteReprRow private (
  name: String,
  distance: Double,
  message: List[String]
)

object SatelliteReprRow {
  implicit def toDomain: Morph[SatelliteReprRow, SatelliteRepr] =
    row => SatelliteRepr.of(row.name, row.distance, row.message)

  implicit def fromDomain: Morph[SatelliteRepr, SatelliteReprRow] =
    satellite =>
      new SatelliteReprRow(
        name = satellite.name.value,
        distance = satellite.distance.value,
        message = satellite.message.words)
}
