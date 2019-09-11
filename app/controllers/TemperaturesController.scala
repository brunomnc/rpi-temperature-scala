package controllers
import models._
import javax.inject._
import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json

import scala.concurrent.{ExecutionContext, Future}

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class TemperaturesController @Inject()(repo: TemperaturesRepository,cc: ControllerComponents) (implicit ec: ExecutionContext)  extends AbstractController(cc) {

  /**
   * Create an Action to render an HTML page with a welcome message.
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  val tempForm: Form[CreateTempForm] = Form {
    mapping(
      "temp" -> number,
      "humidity" -> number
    )(CreateTempForm.apply)(CreateTempForm.unapply)
  }

  def index = Action {
    Ok("ok")
  }

  /**
   * A REST endpoint that gets all the temperatures as JSON.
   */
  def getTemps = Action.async { implicit request =>
    repo.list().map { temps =>
      Ok(Json.toJson(temps))
    }
  }

  def storeTemp: Action[AnyContent] = Action.async { implicit request =>
    tempForm.bindFromRequest.fold(
      // The error function. We return the index page with the error form, which will render the errors.
      errorForm => {
        Future.successful(Status(400))
      },
      // There were no errors in the from, so create the data.
      temps => {
        repo.create(temps.temp, temps.humidity).map { _ =>
          // If successful, we simply redirect to the index page.
          Redirect(routes.TemperaturesController.index()).flashing("success" -> "data.created")
        }
      }
    )
  }
}

case class CreateTempForm(temp: Int, humidity: Int)
