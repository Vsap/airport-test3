package model

import slick.lifted.Tag
import slick.jdbc.PostgresProfile.api._
import scala.concurrent.Future
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global
import java.time.LocalDateTime

case class Trip(
                tripNo: Long,
                companyId: Long,
                plane: String,
                townFrom: String,
                townTo: String,
                timeOut: String,
                timeIn: String)

class TripTable(tag: Tag) extends Table[Trip](tag, "trip"){

  val tripNo = column[Long]("trip_no")
  val companyId = column[Long]("company_id")
  val plane = column[String]("plane")
  val townFrom = column[String]("from")
  val townTo = column[String]("to")
  val timeOut = column[String]("time_out")
  val timeIn = column[String]("time_in")


  val companyFk = foreignKey("company_id_fk", companyId, TableQuery[CompanyTable])(_.id)

  def * = (tripNo, companyId, plane, townFrom, townTo, timeOut, timeIn) <>
    (Trip.apply _ tupled, Trip.unapply)
}

object TripTable{ val table = TableQuery[TripTable]}

class TripRepository(db: Database){
  def create(trip: Trip):Future[Trip] =
    db.run(TripTable.table returning TripTable.table += trip)
  def update(trip: Trip): Future[Int] =
    db.run(TripTable.table.filter(_.tripNo === trip.tripNo).update(trip))
  def delete(trip: Trip): Future[Int] =
    db.run(TripTable.table.filter(_.tripNo === trip.tripNo).delete)
  def getByNo(tripId: Long): Future[Option[Trip]] =
    db.run(TripTable.table.filter(_.tripNo === tripId).result.headOption)
}