package scource

import java.time._
import javafx.util.converter.LocalDateTimeStringConverter
import model._
import scala.io.Source

case class DataConverter(path: String) {
  val source: List[String] = Source.fromFile(path, "UTF-8").getLines.toList

  def getCompany(file: List[String]):List[Company] = {
    val companyPattern = """\((.*),(".*")\)""".r
    for{
      line <- file
      companyPattern(id,name) <- companyPattern.findAllIn(line)
    } yield Company(Option(id.toLong), name)
  }
  def getPassenger(file: List[String]):List[Passenger] = {
    val passengerPattern = """\((.*),(".*")\)""".r
    for{
      line <- file
      passengerPattern(id,name) <- passengerPattern.findAllIn(line)
    } yield Passenger(Option(id.toLong), name)
  }
  def getTrip(file: List[String]):List[Trip] = {
    //val tripPattern = """\((.*),(.*),(".*"),(".*"),(".*"),"((.){4}(.){2}(.){2} (.*):(.*):(.*))","((.){4}(.){2}(.){2} (.*):(.*):(.*))"\)""".r
    val tripPattern = """\((.*),(.*),(".*"),(".*"),(".*"),"(.*)","(.*)"\)""".r
    for{
      line <- file
      if(line != """(-*)(Pass_in_trip)(-*)""")
      //tripPattern(tripNo, companyId, plane, townFrom, townTo, (yO, mO, dO, hO, minsO, sO), (yI, mI, dI, hI, minsI, sI)) <- tripPattern.findAllIn(line)
        tripPattern(tripNo, companyId, plane, townFrom, townTo, timeOut, timeIn) <- tripPattern.findAllIn(line)
    } yield Trip(Option(tripNo.toLong), tripNo.toLong, companyId.toLong, plane,
      //townFrom, townTo, LocalDateTime.of(yO, mO, dO, hO, minsO, sO, 0), LocalDateTime.of(yI, mI, dI, hI, minsI, sI, 0))
        townFrom, townTo, timeOut, timeIn)
  }
  def getPassInTrip(file: List[String]):List[PassInTrip] = {
    val passInTripPattern = """\((.*),(".*"),(.*),"(.*)"\)""".r
    for{
      line <- file
      passInTripPattern(tripId, dateTo, passengerId, place) <- passInTripPattern.findAllIn(line)
    } yield PassInTrip(Option(tripId.toLong), tripId.toLong, dateTo, passengerId.toLong, place)
  }

}
