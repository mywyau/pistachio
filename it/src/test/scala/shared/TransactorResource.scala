package shared

import cats.effect.IO
import configuration.models.AppConfig
import configuration.{ConfigReader, ConfigReaderAlgebra}
import doobie.Transactor
import org.http4s.client.Client
import org.http4s.server.Server

// Define a wrapper case class to help with runtime type issues
case class TransactorResource(xa: Transactor[IO])

case class HttpClientResource(client: Client[IO])
