package models

import play.api.libs.json._

case class Temperatures(id: Long, temperature: Int, humidity: Int)

object Temperatures {
  implicit val tempFormat: OFormat[Temperatures] = Json.format[Temperatures]
}

