import model._
import source._
import slick.jdbc.PostgresProfile.api._
import java.time.{LocalDateTime, ZoneOffset}
import java.sql.Time

import scala.concurrent.Await
import scala.concurrent.duration._

object Main {
  val db = Database.forURL(
    "jdbc:postgresql://127.0.0.1/postgres?user=postgres&password=swordfish"
  )

  val passengerRepository = new PassengerRepository(db)
  val companyRepository = new CompanyRepository(db)
  val tripRepository = new TripRepository(db)
  val passInTripRepository = new PassInTripRepository(db)
  val path = "C:\\Users\\Vladik\\Desktop\\test3\\airport\\src\\main\\scala\\source.txt"
  def main(args: Array[String]): Unit = {
    //init()
    databaseFill(path)
  }

  def init(): Unit = {
    Await.result(db.run(PassengerTable.table.schema.create), Duration.Inf)
    Await.result(db.run(CompanyTable.table.schema.create), Duration.Inf)
    Await.result(db.run(TripTable.table.schema.create), Duration.Inf)
    Await.result(db.run(PassInTripTable.table.schema.create), Duration.Inf)
  }

 def databaseFill(path: String): Unit = {
   val dc = DataConverter(path)
    for (i <- dc.getCompany) {
      Await.result(companyRepository.create(i), Duration.Inf)
    }

    for (i <- dc.getPassenger) {
      Await.result(passengerRepository.create(i), Duration.Inf)
    }

    for (i <- dc.getTrip) {
      Await.result(tripRepository.create(i), Duration.Inf)
    }

    for (i <- dc.getPassInTrip) {
      Await.result(passInTripRepository.create(i), Duration.Inf)
    }
  }
}
