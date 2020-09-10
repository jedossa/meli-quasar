package meli.quasar.domain

final case class SatelliteRepr private (
  name: Name,
  distance: Distance,
  message: Message
)

object SatelliteRepr {
  def of(name: String, distance: Double, message: List[String]): SatelliteRepr =
    new SatelliteRepr(Name(name), Distance(distance), Message(message))
}
