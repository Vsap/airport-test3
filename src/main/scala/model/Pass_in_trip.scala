package model

import java.sql.Date
import java.time.LocalDateTime
import slick.lifted.Tag
import slick.jdbc.PostgresProfile.api._
import scala.concurrent.Future


case class PassInTrip(
                      tripId: Long,
                      date: LocalDateTime,
                      passengerId: Long,
                      place:String)

class PassInTripTable(tag: Tag) extends Table[PassInTrip](tag, "in_trip"){

  def tripId = column[Long]("trip_id")
  def date = column[LocalDateTime]("date")
  def passengerId = column[Long]("passengerId")
  def place = column[String]("place")

  def tripFk = foreignKey("trip_id_fk", tripId, TableQuery[TripTable])(_.tripNo)
  def passengerFk = foreignKey("passenger_id_fk", passengerId, TableQuery[PassengerTable])(_.id)

  def * = (tripId, date, passengerId, place) <>
    (PassInTrip.apply _ tupled, PassInTrip.unapply)
}

object PassInTripTable{
  val table = TableQuery[PassInTripTable]
}

class PassInTripRepository(db: Database){
  val passInTripTableQuery = TableQuery[PassInTripTable]
  def create(passInTrip: PassInTrip): Future[PassInTrip] =
    db.run(passInTripTableQuery returning passInTripTableQuery += passInTrip)
  def update(passInTrip: PassInTrip):Future[Int] =
    db.run(passInTripTableQuery.filter( _.tripId === passInTrip.tripId).update(passInTrip))
  def delete(passInTrip: PassInTrip):Future[Int] =
    db.run(passInTripTableQuery.filter( _.tripId === passInTrip.tripId).delete)
  def getByPassengerId(passId:Long):Future[Option[PassInTrip]] =
    db.run(passInTripTableQuery.filter(_.passengerId === passId).result.headOption)
}