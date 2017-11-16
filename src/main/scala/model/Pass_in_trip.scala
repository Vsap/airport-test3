package model

import java.sql.Date
import java.time.LocalDateTime
import slick.lifted.Tag
import slick.jdbc.PostgresProfile.api._
import scala.concurrent.Future


case class PassInTrip(id: Option[Long],
                      tripId: Long,
                      //date: LocalDateTime,
                      date: String,
                      passengerId: Long,
                      place:String)

class PassInTripTable(tag: Tag) extends Table[PassInTrip](tag, "in_trip"){
  val id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  val tripId = column[Long]("trip_id")
  //val date = column[LocalDateTime]("date")
  val date = column[String]("date") ////correct
  val passengerId = column[Long]("passengerId")
  val place = column[String]("place")

  val tripFk = foreignKey("trip_id_fk", tripId, TableQuery[TripTable])(_.tripNo)
  val passengerFk = foreignKey("passenger_id_fk", passengerId, TableQuery[PassengerTable])(_.id)

  def * = (id.?,tripId, date, passengerId, place) <>
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
    db.run(passInTripTableQuery.filter( _.id === passInTrip.id).update(passInTrip))
  def delete(passInTrip: PassInTrip):Future[Int] =
    db.run(passInTripTableQuery.filter( _.id === passInTrip.id).delete)
  def getById(passInTripId: Long):Future[Option[PassInTrip]] =
    db.run(passInTripTableQuery.filter(_.id === passInTripId).result.headOption)
}