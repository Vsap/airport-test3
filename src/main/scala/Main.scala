import model._
import slick.jdbc.PostgresProfile.api._
import java.time.{LocalDateTime, ZoneOffset}
import java.sql.Time

import scala.concurrent.Await
import scala.concurrent.duration._

object Main {
  val db = Database.forURL(
    "jdbc:postgresql://127.0.0.1/postgres?user=postgres&password=swordfish"
  )

  val passInTripRepository = new PassInTripRepository(db)
  val tripRepository = new TripRepository(db)
  val passengerRepository = new PassengerRepository(db)
  val companyRepository = new CompanyRepository(db)

  def main(args: Array[String]): Unit = {
    init()
    databaseFill()
  }

  def init(): Unit = {
    Await.result(db.run(PassengerTable.table.schema.create), Duration.Inf)
    Await.result(db.run(CompanyTable.table.schema.create), Duration.Inf)
    Await.result(db.run(TripTable.table.schema.create), Duration.Inf)
    Await.result(db.run(PassInTripTable.table.schema.create), Duration.Inf)
  }

  def databaseFill(): Unit = {
    for (i <- 1 to 5) {
      Await.result(companyRepository.create(Company(Some(i), s"Company #$i")), Duration.Inf)
    }

    for (i <- 1 to 100) {
      Await.result(passengerRepository.create(Passenger(Some(i), s"Passenger #$i")), Duration.Inf)
    }

    for (i <- 1 to 50) {
      Await.result(tripRepository.create(Trip(Some(i), i % 5 + 1, s"Plane #$i",
        s"Town From #$i", s"Town To #$i",
          LocalDateTime.ofEpochSecond(i*110,0, ZoneOffset.UTC), LocalDateTime.ofEpochSecond(i*110,0,ZoneOffset.UTC))), Duration.Inf)
    }

    for (i <- 1 to 30) {
      Await.result(passInTripRepository.create(PassInTrip(Some(i), i * i % 50 + 1,
        LocalDateTime.ofEpochSecond(i*110,0,ZoneOffset.UTC), i*i*i % 100 +1, s"Place #$i")), Duration.Inf)
    }
  }
}
