package controllers

import cats.effect.*
import com.comcast.ip4s.{Host, Port, ipv4, port}
import configuration.models.AppConfig
import configuration.{BaseAppConfig, ConfigReader, ConfigReaderAlgebra}
import controllers.TestRoutes.*
import doobie.*
import doobie.hikari.HikariTransactor
import doobie.implicits.*
import doobie.util.ExecutionContexts
import org.http4s.*
import org.http4s.Method.*
import org.http4s.circe.*
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder
import org.http4s.client.Client
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits.*
import org.http4s.server.{Router, Server}
import shared.{HttpClientResource, TransactorResource}
import weaver.{GlobalResource, GlobalWrite}

import scala.concurrent.ExecutionContext

object ControllerSharedResource extends GlobalResource with BaseAppConfig {

  def executionContextResource: Resource[IO, ExecutionContext] =
    ExecutionContexts.fixedThreadPool(4)

  def transactorResource(host: String, port: Int, ce: ExecutionContext): Resource[IO, HikariTransactor[IO]] =
    HikariTransactor.newHikariTransactor[IO](
      driverClassName = "org.postgresql.Driver",
      url = "jdbc:postgresql://localhost:5432/shared_test_db",
      user = sys.env.getOrElse("TEST_DB_USER", "shared_user"),
      pass = sys.env.getOrElse("TEST_DB_PASS", "share"),
      connectEC = ce
    )

  def clientResource: Resource[IO, Client[IO]] =
    EmberClientBuilder.default[IO].build

  def serverResource(
                      host: Host,
                      port: Port,
                      router: HttpRoutes[IO]
                    ): Resource[IO, Server] =
    EmberServerBuilder
      .default[IO]
      .withHost(host)
      .withPort(port)
      .withHttpApp(router.orNotFound)
      .build

  def sharedResources(global: GlobalWrite): Resource[IO, Unit] = {
    for {
      appConfig <- configResource
      host <- hostResource(appConfig)
      port <- portResource(appConfig)
      postgresqlHost <- postgresqlHostResource(appConfig)
      postgresqlPort <- postgresqlPortResource(appConfig)
      ce <- executionContextResource
      xa <- transactorResource(postgresqlHost, postgresqlPort, ce)
      client <- clientResource
      _ <- serverResource(host, port, createTestRouter(xa))
      _ <- global.putR(TransactorResource(xa))
      _ <- global.putR(HttpClientResource(client))
    } yield ()
  }
}