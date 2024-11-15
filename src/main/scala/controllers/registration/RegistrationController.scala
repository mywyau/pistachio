package controllers.registration

import cats.data.Validated.{Invalid, Valid}
import cats.effect.unsafe.implicits.global
import cats.effect.{Concurrent, IO}
import cats.implicits.*
import io.circe.syntax.EncoderOps
import models.auth.{RegisterEmailErrors, RegisterPasswordErrors, RegisterUsernameErrors}
import models.users.wanderer_profile.requests.UserSignUpRequest
import models.users.wanderer_profile.responses.CreatedUserResponse
import models.users.registration.responses.error.RegistrationErrorResponse
import org.http4s.*
import org.http4s.circe.*
import org.http4s.dsl.Http4sDsl
import services.auth.algebra.*
import services.registration.RegistrationServiceAlgebra


trait RegistrationController[F[_]] {
  def routes: HttpRoutes[F]
}

class RegistrationControllerImpl[F[_] : Concurrent](
                                                     registrationService: RegistrationServiceAlgebra[F]
                                                   ) extends Http4sDsl[F] with RegistrationController[F] {

  implicit val userSignUpRequestDecoder: EntityDecoder[F, UserSignUpRequest] = jsonOf[F, UserSignUpRequest]

  val routes: HttpRoutes[F] = HttpRoutes.of[F] {

    case req@POST -> Root / "register" =>
      IO(println("Received a POST /register request")).unsafeRunSync() // Print immediately
      req.decode[UserSignUpRequest] { request =>
        IO(println(s"Received UserSignUpRequest: $request")).unsafeRunSync() // Log incoming payload
        registrationService.registerUser(request).flatMap {
          case Valid(user) =>
            Created(CreatedUserResponse("User created successfully").asJson)
          case Invalid(errors) => {
            val usernameErrors =
              errors.collect {
                case e: RegisterUsernameErrors => e.errorMessage
              }

            val passwordErrors =
              errors.collect {
                case e: RegisterPasswordErrors => e.errorMessage
              }
            
            val emailErrors =
              errors.collect {
                case e: RegisterEmailErrors => e.errorMessage
              }

            val errorResponse =
              RegistrationErrorResponse(
                usernameErrors = usernameErrors,
                passwordErrors = passwordErrors,
                emailErrors = emailErrors
              )

            BadRequest(errorResponse.asJson)
          }
        }
      }
  }
}

object RegistrationController {
  def apply[F[_] : Concurrent](
                                registrationService: RegistrationServiceAlgebra[F]
                              ): RegistrationController[F] =
    new RegistrationControllerImpl[F](registrationService)
}
