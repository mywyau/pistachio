package controllers.login

import cats.data.Validated.{Invalid, Valid}
import cats.effect.Concurrent
import cats.implicits.*
import io.circe.syntax.EncoderOps
import models.authentication.login.adts.{LoginPasswordError, LoginUsernameError}
import models.authentication.login.errors.LoginErrorResponse
import models.authentication.login.requests.UserLoginRequest
import models.authentication.login.responses.LoginResponse
import models.responses.ErrorResponse
import org.http4s.*
import org.http4s.circe.*
import org.http4s.dsl.Http4sDsl
import org.typelevel.log4cats.Logger
import services.authentication.login.LoginServiceAlgebra


trait LoginController[F[_]] {
  def routes: HttpRoutes[F]
}

class LoginControllerImpl[F[_] : Concurrent](
                                              loginService: LoginServiceAlgebra[F]
                                            )(implicit logger: Logger[F])
  extends Http4sDsl[F] with LoginController[F] {

  implicit val userLoginDecoder: EntityDecoder[F, UserLoginRequest] = jsonOf[F, UserLoginRequest]

  val routes: HttpRoutes[F] = HttpRoutes.of[F] {
    case req@POST -> Root / "login" =>
      logger.info(s"[LoginControllerImpl] POST - A user is attempting to log in") *>
        req.decode[UserLoginRequest] { request =>
          loginService.loginUser(request).flatMap {
            case Valid(userLoginDetails) =>
              logger.info(s"[LoginControllerImpl] POST - A successful login") *>
              Ok(
                LoginResponse(
                  userId = userLoginDetails.userId,
                  username = userLoginDetails.username,
                  passwordHash = userLoginDetails.passwordHash,
                  email = userLoginDetails.email,
                  role = userLoginDetails.role
                ).asJson)
            case Invalid(errors) =>

              val usernameErrors = errors.collect {
                case error: LoginUsernameError => ErrorResponse(error.code, error.message)
              }

              val passwordErrors = errors.collect {
                case error: LoginPasswordError => ErrorResponse(error.code, error.message)
              }
            
              BadRequest(
                LoginErrorResponse(
                  usernameErrors = usernameErrors,
                  passwordErrors = passwordErrors
                ).asJson)
          }
        }
  }
}

object LoginController {
  def apply[F[_] : Concurrent](
                                loginService: LoginServiceAlgebra[F]
                              )(implicit logger: Logger[F]): LoginController[F] =
    new LoginControllerImpl[F](loginService)
}

