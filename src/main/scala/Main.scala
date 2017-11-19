import model._
import source._
import slick.jdbc.PostgresProfile.api._
import java.sql._
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

    val task63 = for { ((pp1, tId1),(pp2, tId2)) <- PassInTripTable.table.groupBy(p => (p.passengerId, p.place)).map{case (psgId, group) =>
      (psgId, group.map(p => p.tripId))}.map{ case (pp1,tId1) => ((pp1,tId1),PassInTripTable.table.groupBy(p =>
      (p.passengerId, p.place)).map{case (pp2,group) => (pp2,group.map(p => p.tripId))})} if(pp1 === pp2 && tId1 =!= tId2)}{yield pp1}

    def task67 = {
      val temp = TripTable.table.groupBy(p => (p.townFrom, p.townTo)).map{case (_,group) => (group.length)}.sortBy(_.desc).take(1)
      TripTable.table.groupBy(p => (p.townFrom, p.townTo)).map{case (_,group) => (group.length)}.sortBy(_.desc).filterNot(_ === temp).length
    }
    val task68 = Trip
  }
  //val task63 = db.run(PassengerTable.table.filter(_.id === ().id ))
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
