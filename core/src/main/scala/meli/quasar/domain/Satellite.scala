package meli.quasar.domain

final case class Satellite private (
  name: Name,
  location: Point,
  message: Message
)

final case class Name(value: String) extends AnyVal

object Satellite {
  def of(name: String, x: Double, y: Double, message: List[String]): Satellite =
    new Satellite(Name(name), Point(x, y), Message(message))
}
