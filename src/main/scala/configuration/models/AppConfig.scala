package configuration.models

import pureconfig.ConfigReader
import pureconfig.generic.derivation.*

case class ServerConfig(host: String, port: Int) derives ConfigReader

case class IntegrationSpecConfig(
                                  host: String,
                                  port: Int,
                                  postgresHost: String,
                                  postgresPort: Int,
                                ) derives ConfigReader

case class AppConfig(
                      serverConfig: ServerConfig,
                      integrationSpecConfig: IntegrationSpecConfig
                    ) derives ConfigReader
