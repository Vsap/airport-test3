package model

import java.sql.Date

import slick.lifted.Tag
import slick.jdbc.PostgresProfile.api._
import scala.concurrent.Future


case class PassInTrip(
    id: Option[Long],tripId: Long, date: Date, passengerId: Long, place:String)

class PassInTripTable(tag: Tag) extends Table[PassInTrip](tag, "in_trip"){
  val id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  val tripId = column[Long]("trip_id")
  val date = column[Date]("date") ////correct
  val passengerId = column[Long]("passengerId") ////to correct
  val place = column[String]("place")

  val tripFk = foreignKey("trip_id_fk", tripId, TableQuery[TripTable])(_.id)
  val passengerFk = foreignKey("passenger_id_fk", passengerId, TableQuery[PassengerTable])(_.id)

  def * = (id.?,tripId, date, passengerId, place) <>
    (PassInTrip.apply _ tupled, PassInTrip.unapply)
}
