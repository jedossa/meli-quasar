package meli.quasar.infrastructure.repository.rows

import meli.quasar.domain.Satellite
import meli.quasar.generators.BaseGenerator
import meli.quasar.ops.morpher._
import meli.quasar.repository.rows.SatelliteRow
import org.scalatest.funsuite.AnyFunSuite
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks

class RowMorphismTest extends AnyFunSuite with ScalaCheckDrivenPropertyChecks with BaseGenerator {
  test("Morphs should be reversible") {
    forAll(kenobi) { satellite: Satellite =>
      assert(satellite.to[SatelliteRow].to[Satellite] == satellite)
    }

    forAll(skywalker) { satellite: Satellite =>
      assert(satellite.to[SatelliteRow].to[Satellite] == satellite)
    }

    forAll(sato) { satellite: Satellite =>
      assert(satellite.to[SatelliteRow].to[Satellite] == satellite)
    }
  }
}
