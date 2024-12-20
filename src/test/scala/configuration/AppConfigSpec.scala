package configuration

import cats.effect.IO
import configuration.models.{AppConfig, IntegrationSpecConfig, ServerConfig}
import weaver.SimpleIOSuite

object AppConfigSpec extends SimpleIOSuite {

  val configReader: ConfigReaderAlgebra[IO] = ConfigReader[IO]

  test("load test configurations correctly") {

    val serverConfig = ServerConfig("0.0.0.0", 8081)
    val integrationSpecConfig =
      IntegrationSpecConfig(
        host = "127.0.0.1",
        port = 9999,
        postgresHost = "localhost",
        postgresPort = 5432,
      )
    val appConfig = AppConfig(serverConfig, integrationSpecConfig)

    for {
      config <- configReader.loadAppConfig
    } yield {
      expect.all(
        config.serverConfig == serverConfig,
        config.integrationSpecConfig == integrationSpecConfig,
        config == appConfig
      )
    }
  }
}
