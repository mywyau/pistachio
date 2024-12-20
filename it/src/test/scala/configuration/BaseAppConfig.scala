package configuration

import cats.effect.*
import com.comcast.ip4s.{Host, Port, ipv4, port}
import configuration.models.AppConfig
import configuration.{ConfigReader, ConfigReaderAlgebra}

trait BaseAppConfig {

  val configReader: ConfigReaderAlgebra[IO] = ConfigReader[IO]

  def configResource: Resource[IO, AppConfig] = {
    Resource.eval(
      configReader
        .loadAppConfig
        .handleErrorWith { e =>
          IO.raiseError(new RuntimeException(s"[ControllerSharedResource] Failed to load app configuration: ${e.getMessage}", e))
        }
    )
  }

  def hostResource(appConfig: AppConfig): Resource[IO, Host] = {
    Resource.eval(
      IO.fromEither(
        Host.fromString(appConfig.integrationSpecConfig.host)
          .toRight(new RuntimeException("[ControllerSharedResource] Invalid host configuration"))
      )
    )
  }

  def portResource(appConfig: AppConfig): Resource[IO, Port] = {
    Resource.eval(
      IO.fromEither(
        Port.fromInt(appConfig.integrationSpecConfig.port)
          .toRight(new RuntimeException("[ControllerSharedResource] Invalid port configuration"))
      )
    )
  }

  def postgresqlHostResource(appConfig: AppConfig): Resource[IO, String] = {
    Resource.eval(
      IO(appConfig.integrationSpecConfig.postgresHost)
    )
  }

  def postgresqlPortResource(appConfig: AppConfig): Resource[IO, Int] = {
    Resource.eval(
      IO(appConfig.integrationSpecConfig.postgresPort)
    )
  }
}

