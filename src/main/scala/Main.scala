import model._
import source._
import slick.jdbc.PostgresProfile.api._
import scala.concurrent.Await
import scala.concurrent.duration._

object Main {
  val db = Database.forURL(
    "jdbc:postgresql://127.0.0.1/postgres?user=postgres&password=sworfish"
  )
  val passengerRepository = new PassengerRepository(db)
  val companyRepository = new CompanyRepository(db)
  val tripRepository = new TripRepository(db)
  val passInTripRepository = new PassInTripRepository(db)
  val path = "source.txt"
  def main(args: Array[String]): Unit = {

    def task63 = PassInTripTable.table.join(PassengerTable.table).on(_.passengerId === _.id)
      .groupBy{case (psgT,psg) => (psgT.place, psg.name)}
      .map{ case ((place, name), group) => (name, group.length)}
      .filter{ case (name, count) => count > 1 }
      .map{case (name, _ ) => name}.result

    def task67 = TripTable.table.groupBy(p =>
      (p.townFrom, p.townTo)).map{case (_,group) => group.length}.sortBy(_.desc).take(1).result

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

    def task95 = PassInTripTable.table.join(TripTable.table).on(_.tripId == _.tripNo)
      .join(CompanyTable.table).on{case ((pit,trip), comp) =>  trip.companyId === comp.id}

    def task72 = PassengerTable.table.join(PassInTripTable.table).on(_.id === _.passengerId).
      join(TripTable.table).on{ case ((psg,psgT),trip) => psgT.tripId === trip.tripNo}.
      groupBy{case (((psg,psgInTrip),trip)) => psg.name}.map{case (name,trips) =>
      (name,trips.length)}.sortBy(_._2.desc).groupBy(_._2).take(1).result


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
