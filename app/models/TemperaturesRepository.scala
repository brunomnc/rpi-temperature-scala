package models

import javax.inject.{Inject, Singleton}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

/**
 * @param dbConfigProvider The Play db config provider. Play will inject this for you.
 */
@Singleton
class TemperaturesRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile] {
  // We want the JdbcProfile for this provider
//  private val dbConfig = dbConfigProvider.get[JdbcProfile]
//  println("###################################################")
//  println(dbConfig.config)

  // These imports are important, the first one brings db into scope, which will let you do the actual db operations.
  // The second one brings the Slick DSL into scope, which lets you define the table and other queries.
//  import dbConfig._
  import profile.api._

  /**
   * Here we define the table. It will have a name of people
   */
  private class TemperaturesTable(tag: Tag) extends Table[Temperatures](tag, "Temperatures") {

    /** The ID column, which is the primary key, and auto incremented */
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    /** The temperature column */
    def temperature = column[Int]("temperature")

    /** The humidity column */
    def humidity = column[Int]("humidity")

    def * = (id, temperature, humidity) <> ((Temperatures.apply _).tupled, Temperatures.unapply)
  }

  private val temperatureTable = TableQuery[TemperaturesTable]

  /**
   * Create a temperature row with the given temp and humidity.
   *
   * This is an asynchronous operation, it will return a future of the created person, which can be used to obtain the
   * id for that person.
   */
  def create(temperature: Int, humidity: Int): Future[Temperatures] = db.run {
    (temperatureTable.map(p => (p.temperature, p.humidity))
      returning temperatureTable.map(_.id)
      into ((tempHum, id) => Temperatures(id, tempHum._1, tempHum._2))
      ) += (temperature, humidity)
  }

  /**
   * List all the temperature in the database.
   */
  def list(): Future[Seq[Temperatures]] = db.run {
    temperatureTable.result
  }
}