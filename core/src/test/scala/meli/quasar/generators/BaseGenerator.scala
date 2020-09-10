package meli.quasar.generators

import meli.quasar.domain._

trait BaseGenerator {
  val kenobi: Satellite = Satellite(Name("kenobi"), Point(-500d, -200d), Message(Nil))
  val skywalker: Satellite = Satellite(Name("skywalker"), Point(100d, -100d), Message(Nil))
  val sato: Satellite = Satellite(Name("sato"), Point(500d, 100d), Message(Nil))
}
