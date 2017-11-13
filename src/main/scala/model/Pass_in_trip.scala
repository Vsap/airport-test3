package model

import java.sql.Date

import slick.lifted.Tag
import slick.jdbc.PostgresProfile.api._
import scala.concurrent.Future


case class PassInTrip(
    id: Option[Long], date: Date, passengerId: Long, place:String)

class PassInTripTable(tag: Tag) extends Table[PassInTrip](tag, "in_trip"){
  val id = column[Long]("id", O.PrimaryKey, O.PrimaryKey)
  val date = column[Date]("date") ////correct
  val passengerId = column[Long]("passengerId") ////to correct
  val place = column[String]("place")
  def * = (id.?, date, passengerId, place) <>
    (PassInTrip.apply _ tupled, PassInTrip.unapply)
}
