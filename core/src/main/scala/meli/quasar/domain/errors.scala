package meli.quasar.domain

final case class ErrorMessage private (message: String) extends AnyVal

sealed abstract class DomainError(val error: ErrorMessage) extends Exception(error.message)

final case class InsufficientInformation(
  override val error: ErrorMessage = ErrorMessage("No hay suficiente informacion")
) extends DomainError(error)

final case class NoSatellites(
  override val error: ErrorMessage = ErrorMessage("No hay satelites en servicio")
) extends DomainError(error)

final case class NoMessage(
  override val error: ErrorMessage = ErrorMessage("No hay mensaje")
) extends DomainError(error)
