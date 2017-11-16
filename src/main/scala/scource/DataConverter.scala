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
    val tripPattern = """\((.*),(.*),(".*"),(".*"),(".*"),"((.){4}(.){2}(.){2} (.*):(.*):(.*))","((.){4}(.){2}(.){2} (.*):(.*):(.*))"\)""".r
    for{
      line <- file
      tripPattern(tripNo, companyId, plane, townFrom, townTo, (yO, mO, dO, hO, minsO, sO), (yI, mI, dI, hI, minsI, sI)) <- tripPattern.findAllIn(line)
    } yield Trip(Option(tripNo.toLong), tripNo.toLong, companyId.toLong, plane,
      townFrom, townTo, LocalDateTime.of(yO, mO, dO, hO, minsO, sO, 0), LocalDateTime.of(yI, mI, dI, hI, minsI, sI, 0))
  }
  LocalDateTime.
  def getPassInTrip(file: List[String]):List[(String, String, String, String)] = {
    val tripPattern = """\((.*),(.*),(".*"),(".*"),(".*"),"(.*)","(.*)"\)""".r
    for{
      line <- file
      tripPattern(tripId, dateTo, passengerId, place) <- tripPattern.findAllIn(line)
    } yield (tripId, dateTo, passengerId, place)
  }

}
