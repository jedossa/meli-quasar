package meli.quasar.dtos

import io.circe.parser.decode
import io.circe.syntax._
import meli.quasar.generators.CoreItGenerator
import org.scalatest.funsuite.AnyFunSuite
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks

class SatelliteDtoIt extends AnyFunSuite with ScalaCheckDrivenPropertyChecks with CoreItGenerator {
  test("Should show as expected json") {
    forAll(accountWithActiveCard) { account =>
      assert(
        account.asJson.noSpaces == s"""{"account":{"active-card":${account.activeCard},"available-limit":${account.availableLimit}},"violations":[]}""".stripMargin)
    }

    forAll(accountWithInactiveCard) { account =>
      assert(account.asJson.noSpaces ==
        s"""{"account":{"active-card":${account.activeCard},"available-limit":${account.availableLimit}},"violations":[]}""".stripMargin)
    }

    forAll(accountWithViolations) { account =>
      assert(account.asJson.noSpaces ==
        s"""{"account":{"active-card":${account.activeCard},"available-limit":${account.availableLimit}},"violations":["${account.violations.headOption
             .getOrElse("")}"]}""".stripMargin)
    }
  }

  test("Should parse json as DTO") {
    forAll(accountWithActiveCard) { account =>
      val json =
        s"""{"account":{"active-card":${account.activeCard},"available-limit":${account.availableLimit}},"violations":[]}""".stripMargin
      decode[TopSecretDTO](json).fold(e => fail(e.getMessage), dto => assert(account === dto))
    }

    forAll(accountWithInactiveCard) { account =>
      val json =
        s"""{"account":{"active-card":${account.activeCard},"available-limit":${account.availableLimit}},"violations":[]}""".stripMargin
      decode[TopSecretDTO](json).fold(e => fail(e.getMessage), dto => assert(account === dto))
    }

    forAll(accountWithViolations) { account =>
      val json =
        s"""{"account":{"active-card":${account.activeCard},"available-limit":${account.availableLimit}},"violations":[]}""".stripMargin
      decode[TopSecretDTO](json).fold(e => fail(e.getMessage), dto => assert(account.copy(violations = Nil) === dto))
    }
  }
}
