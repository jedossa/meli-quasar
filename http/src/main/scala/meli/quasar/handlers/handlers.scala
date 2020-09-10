/*
 * Copyright (c) S4N 2019
 */

package meli.quasar

import cats.Applicative
import io.circe.generic.semiauto.deriveEncoder
import io.circe.{Encoder, Json}
import meli.quasar.domain._
import org.http4s.EntityEncoder
import org.http4s.circe.jsonEncoderOf

package object handlers {
  private[handlers] implicit val domainError: Encoder[DomainError] =
    (input: DomainError) =>
      Json.obj(
        "error" -> Json.obj(
          "message" -> Json.fromString(input.error.message)
        )
      )

  private[handlers] implicit val errorMessage: Encoder[ErrorMessage] =
    deriveEncoder[ErrorMessage]

  private[handlers] implicit def domainErrorEncoder[F[_]: Applicative]: EntityEncoder[F, DomainError] =
    jsonEncoderOf[F, DomainError]
}
