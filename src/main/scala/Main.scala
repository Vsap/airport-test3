import model._
import source._
import slick.jdbc.PostgresProfile.api._
import java.time.temporal.ChronoUnit
import scala.concurrent.Await
import scala.concurrent.duration._
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object Main {
  val db = Database.forURL(
    "jdbc:postgresql://127.0.0.1/postgres?user=postgres&password=swordfish"
  )
  val passengerRepository = new PassengerRepository(db)
  val companyRepository = new CompanyRepository(db)
  val tripRepository = new TripRepository(db)
  val passInTripRepository = new PassInTripRepository(db)
  val path = "source.txt"
  def main(args: Array[String]): Unit = {
    init()
    databaseFill(path)
    task63
    task67
    task72
    task77
    task88
    //task95
    task102
    task103
    task114
  }

  def task63 = PassInTripTable.table.join(PassengerTable.table).on(_.passengerId === _.id)
    .groupBy{case (psgT,psg) => (psgT.place, psg.name)}
    .map{ case ((place, name), group) => (name, group.length)}
    .filter{ case (name, count) => count > 1 }
    .map{case (name, _ ) => name}.result

  def task66 =  PassInTripTable.table.join(TripTable.table).on(_.tripId === _.tripNo)
    .filter{case (psgT, trips) => trips.townFrom === "Rostov"}
    .filter{case (psgT, trips) => psgT.date < LocalDateTime.parse("2003-04-07T00:00")}
    .filter{case (psgT, trips) => psgT.date > LocalDateTime.parse("2003-04-01T00:00")}
    .groupBy(p => (p._1.date))
    .map{case (date, group) => (date, group.length)}.result

  def task67 = TripTable.table.groupBy(p =>
    (p.townFrom, p.townTo)).map{case (_,group) => group.length}.sortBy(_.desc).take(1).result

  def task72 = PassengerTable.table.join(PassInTripTable.table).on(_.id === _.passengerId).
    join(TripTable.table).on{ case ((psg,psgT),trip) => psgT.tripId === trip.tripNo}.
    groupBy{case (((psg,psgInTrip),trip)) => psg.name}.map{case (name,trips) =>
    (name,trips.length)}.sortBy(_._2.desc).take(1).result

  def task77 = TripTable.table.filter(_.townFrom === "Rostov").join(PassInTripTable.table).on(_.tripNo === _.tripId).
    map{ case (trip, passIT) => (trip.tripNo,passIT.date)}.groupBy(p => p._2).
    map{case (_,group) => group.length}.sortBy(_.desc).result

//  def task79 = PassengerTable.table.join(PassInTripTable.table).on(_.id === _.passengerId).
//    join(TripTable.table).on{ case ((psg,psgT),trip) => psgT.tripId === trip.tripNo}
//    .map{case ((psg,psgT),trip) => (psg.name,psgT, ChronoUnit.MINUTES.between(trip.timeIn,trip.timeOut))}.result


  def task84 = {
    val period1 = PassInTripTable.table.join(TripTable.table).on(_.tripId === _.tripNo)
      .filter{case (psgT, trips) => psgT.date < LocalDateTime.parse("2003-04-10T00:00")}
      .filter{case (psgT, trips) => psgT.date > LocalDateTime.parse("2003-04-01T00:00")}
      .groupBy(p => p._2.companyId)
      .map{case (companyId, group) => (companyId, group.length)}
    val period2 = PassInTripTable.table.join(TripTable.table).on(_.tripId === _.tripNo)
      .filter{case (psgT, trips) => psgT.date < LocalDateTime.parse("2003-04-20T00:00")}
      .filter{case (psgT, trips) => psgT.date > LocalDateTime.parse("2003-04-11T00:00")}
      .groupBy(p => p._2.companyId)
      .map{case (companyId, group) => (companyId, group.length)}

    val period3 = PassInTripTable.table.join(TripTable.table).on(_.tripId === _.tripNo)
      .filter{case (psgT, trips) => psgT.date < LocalDateTime.parse("2003-04-30T00:00")}
      .filter{case (psgT, trips) => psgT.date > LocalDateTime.parse("2003-04-21T00:00")}
      .groupBy(p => p._2.companyId)
      .map{case (companyId, group) => (companyId, group.length)}
    (period1 ++ period2 ++ period3).result}

  def task88 = PassengerTable.table.join(PassInTripTable.table).on(_.id === _.passengerId).
    join(TripTable.table).on{ case ((psg,psgT),trip) => psgT.tripId === trip.tripNo}.
    join(CompanyTable.table).on{ case (((_,_),trip),cmp) => trip.companyId === cmp.id}.
    groupBy{case (((psg,psgInTrip),trip),cmp) => (psg.name,cmp.name)}.map{case ((name,cmp),trips) =>
    (name,cmp,trips.length)}.sortBy(_._3.desc).result

//  def task95 = {
//    val completeTable = PassInTripTable.table.join(TripTable.table).on(_.tripId === _.tripNo)
//      .join(CompanyTable.table).on{case ((pit,trip), comp) =>  trip.companyId === comp.id}
//      .map{case ((pit,trip),cmp) => (cmp,trip,pit)}
//    (for{
//      company <- CompanyTable.table
//      flightsAndPassengers <- completeTable.groupBy{case (cmp,trip,pit) => (cmp.name, pit.date)}
//      planes <- CompanyTable.table.join(TripTable.table).on(_.id === _.companyId).groupBy{case (cmp,trips) =>
//        (cmp.id,trips.plane)}
//      //passengers <- completeTable.groupBy{case (cmp,trip,pit) => (cmp.name, pit.date)}
//    }yield (company.name,flightsAndPassengers._1._2.length,planes._2.length,
//      flightsAndPassengers._2.distinct.length,flightsAndPassengers._2.length)).sortBy(_._1).result
//  }

  def task102 = PassengerTable.table.join(PassInTripTable.table).on(_.id === _.passengerId).
    join(TripTable.table).on{ case ((psg,psgT),trip) => psgT.tripId === trip.tripNo}
    .map{case ((psg,inTtrip),trip) => (psg.name, trip.townFrom, trip.townTo)}
    .groupBy(_._1).map{case (name, path) => (name, path.length)}.filter(_._2 === 1).map(_._1).result

  def task103 = (TripTable.table.sortBy(_.tripNo.desc).take(3).map(_.tripNo) ++
    TripTable.table.sortBy(_.tripNo.asc).take(3).map(_.tripNo)).sortBy(_.asc).result

  def task107 = CompanyTable.table.join(TripTable.table).on(_.id === _.companyId).
    join(PassInTripTable.table).on{ case ((cmp,trips),inTrip) => trips.tripNo === inTrip.tripId}
    .filter{case ((_,trip),_) => trip.townFrom === "Rostov"}
    .filter{case ((_,_),inTrip) => inTrip.date < LocalDateTime.parse("2003-05-01T00:00")}
    .filter{case ((_,_),inTrip) => inTrip.date > LocalDateTime.parse("2003-04-01T00:00")}
    .groupBy{case ((cmp,trips),inTrip) => (cmp.name,inTrip.tripId,inTrip.date)}.map(_._1).sortBy(_._3.asc).drop(4).take(1).result

  def task114 = PassInTripTable.table.join(PassengerTable.table).on(_.passengerId === _.id)
    .groupBy{case (psgT,psg) => (psgT.place, psg.name)}
    .map{ case ((place, name), group) => (name, group.length)}
    .filter{ case (name, count) => count > 1 }.sortBy(_._2.desc).result



  def init(): Unit = {
    Await.result(db.run(PassengerTable.table.schema.create), Duration.Inf)
    Await.result(db.run(CompanyTable.table.schema.create), Duration.Inf)
    Await.result(db.run(TripTable.table.schema.create), Duration.Inf)
    Await.result(db.run(PassInTripTable.table.schema.create), Duration.Inf)
  }
  def databaseClear(path: String): Unit = {
    val dc = DataConverter(path)
    for (i <- dc.getPassInTrip) {
      Await.result(passInTripRepository.delete(i), Duration.Inf)
    }

    for (i <- dc.getTrip) {
      Await.result(tripRepository.delete(i), Duration.Inf)
    }


    for (i <- dc.getPassenger) {
      Await.result(passengerRepository.delete(i), Duration.Inf)
    }

    for (i <- dc.getCompany) {
      Await.result(companyRepository.delete(i), Duration.Inf)
    }
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
