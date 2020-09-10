package meli.quasar.dtos

import meli.quasar.domain._
import meli.quasar.ops.Morph

final case class TopSecretDTO(
  satellites: List[SatelliteDTO]
)

object TopSecretDTO {
  implicit def toLocationRequest: Morph[TopSecretDTO, List[(Name, Distance)]] =
    _.satellites.map(satellite => Name(satellite.name) -> Distance(satellite.distance))

  implicit def toMessageRequest: Morph[TopSecretDTO, List[Message]] =
    _.satellites.map(satellite => Message(satellite.message))
}

final case class TopSecretSplitDTO(
  distance: Double,
  message: List[String]
)

object TopSecretSplitDTO {
  implicit def toDomain: Morph[(String, TopSecretSplitDTO), SatelliteRepr] = {
    case (name, secret) => SatelliteRepr(Name(name), Distance(secret.distance), Message(secret.message))
  }
}

final case class SatelliteDTO(
  name: String,
  distance: Double,
  message: List[String]
)

object SatelliteDTO {
  implicit def fromRepr: Morph[SatelliteRepr, SatelliteDTO] =
    repr => SatelliteDTO(repr.name.value, repr.distance.value, repr.message.words)
}

final case class TopResponseDTO(
  position: PointDTO,
  message: String
)

object TopResponseDTO {
  implicit def fromDomain: Morph[(Point, Message), TopResponseDTO] = {
    case (point, message) => TopResponseDTO(PointDTO.fromDomain.to(point), message.words.mkString(" "))
  }
}

final case class PointDTO(
  x: Double,
  y: Double
)

object PointDTO {
  implicit def fromDomain: Morph[Point, PointDTO] = point => PointDTO(point.x, point.y)
}
