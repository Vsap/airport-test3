import model._
import source._
import slick.jdbc.PostgresProfile.api._
import scala.concurrent.Await
import scala.concurrent.duration._

object Main {
  val db = Database.forURL(
    "jdbc:postgresql://127.0.0.1/airport?user=postgres&password=root"
  )
  val passengerRepository = new PassengerRepository(db)
  val companyRepository = new CompanyRepository(db)
  val tripRepository = new TripRepository(db)
  val passInTripRepository = new PassInTripRepository(db)
  val path = "source.txt"
  def main(args: Array[String]): Unit = {

    init()
    databaseFill(path)
    /*def task63 = for { ((pp1, tId1),(pp2, tId2)) <- PassInTripTable.table.groupBy(p => (p.passengerId, p.place)).map{case (psgId, group) =>
      (psgId, group.map(p => p.tripId))}.map{ case (pp1,tId1) => ((pp1,tId1),PassInTripTable.table.groupBy(p =>
      (p.passengerId, p.place)).map{case (pp2,group) => (pp2,group.map(p => p.tripId))})} if(pp1 === pp2 && tId1 =!= tId2)}{yield pp1}
*/
   /* def task63 = PassInTripTable.table.join(PassengerTable.table).on(_.passengerId === _.id).
      map{ case (inTrip, passenger) => (passenger.id, passenger.name, inTrip.place)}.
      groupBy{case (id,name,place) => (id,name,place)}.filter{case (id,name,place) => place}*/

    def task67 = TripTable.table.groupBy(p => (p.townFrom, p.townTo)).map{case (_,group) => (group.length)}.sortBy(_.desc).take(1).result

    def task77 = TripTable.table.filter(_.townFrom === "Rostov").join(PassInTripTable.table).on(_.tripNo === _.tripId).
      map{ case (trip, passIT) => (trip.tripNo,passIT.date)}.groupBy(p => p._2).
        map{case (_,group) => group.length}.sortBy(_.desc)

    def task88 = PassengerTable.table.join(PassInTripTable.table).on(_.id === _.passengerId).
      join(TripTable.table).on{ case ((psg,psgT),trip) => psgT.tripId === trip.tripNo}.
      join(CompanyTable.table).on{ case (((_,_),trip),cmp) => trip.companyId === cmp.id}.
      groupBy{case (((psg,psgInTrip),trip),cmp) => (psg.name,cmp.name)}.map{case ((name,cmp),trips) =>
      (name,cmp,trips.length)}.sortBy(_._3.desc).result

    def task103 = (TripTable.table.sortBy(_.tripNo.desc).take(3).map(_.tripNo) ++
      TripTable.table.sortBy(_.tripNo.asc).take(3).map(_.tripNo)).sortBy(_.asc).result

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
