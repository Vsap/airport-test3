import model._
import source._
import slick.jdbc.PostgresProfile.api._
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
  val path = "source.txt"
  def main(args: Array[String]): Unit = {
    init()
    //databaseClear(path)
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
//    println(task63.toString)
//    println(task67.toString)
//    println(task72.toString)
//    println(task77.toString)
//    println(task88.toString)
//    println(task95.toString)
//    println(task102.toString)
//    println(task103.toString)
  }

  def task63 = PassInTripTable.table.join(PassengerTable.table).on(_.passengerId === _.id)
    .groupBy{case (psgT,psg) => (psgT.place, psg.name)}
    .map{ case ((place, name), group) => (name, group.length)}
    .filter{ case (name, count) => count > 1 }
    .map{case (name, _ ) => name}.result

  def task67 = TripTable.table.groupBy(p =>
    (p.townFrom, p.townTo)).map{case (_,group) => group.length}.sortBy(_.desc).take(1).result

  def task77 = TripTable.table.filter(_.townFrom === "Rostov").join(PassInTripTable.table).on(_.tripNo === _.tripId).
    map{ case (trip, passIT) => (trip.tripNo,passIT.date)}.groupBy(p => p._2).
    map{case (_,group) => group.length}.sortBy(_.desc).result

  def task88 = PassengerTable.table.join(PassInTripTable.table).on(_.id === _.passengerId).
    join(TripTable.table).on{ case ((psg,psgT),trip) => psgT.tripId === trip.tripNo}.
    join(CompanyTable.table).on{ case (((_,_),trip),cmp) => trip.companyId === cmp.id}.
    groupBy{case (((psg,psgInTrip),trip),cmp) => (psg.name,cmp.name)}.map{case ((name,cmp),trips) =>
    (name,cmp,trips.length)}.sortBy(_._3.desc).result

  def task103 = (TripTable.table.sortBy(_.tripNo.desc).take(3).map(_.tripNo) ++
    TripTable.table.sortBy(_.tripNo.asc).take(3).map(_.tripNo)).sortBy(_.asc).result

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

  def task72 = PassengerTable.table.join(PassInTripTable.table).on(_.id === _.passengerId).
    join(TripTable.table).on{ case ((psg,psgT),trip) => psgT.tripId === trip.tripNo}.
    groupBy{case (((psg,psgInTrip),trip)) => psg.name}.map{case (name,trips) =>
    (name,trips.length)}.sortBy(_._2.desc).take(1).result

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
