package meli.quasar.domain

import cats.Eq

final case class Point private (x: Double, y: Double)

object Point {
  def distance(p1: Point, p2: Point): Distance = Distance {
    val x2 = Math.pow(p1.x - p2.x, 2)
    val y2 = Math.pow(p1.y - p2.y, 2)
    Math.sqrt(x2 + y2)
  }
}

final case class Distance private (value: Double) extends AnyVal

object Distance {
  implicit def eq: Eq[Distance] = Eq.fromUniversalEquals
}
