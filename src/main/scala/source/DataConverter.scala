package source

import java.time._
import java.time.format.DateTimeFormatter
import javafx.util.converter.LocalDateTimeStringConverter
import com.sun.org.apache.xerces.internal.impl.xpath.regex.RegularExpression
import model._

import scala.io.Source

case class DataConverter(path: String) {
  val file: List[String] = Source.fromFile(path, "UTF-8").getLines.toList
  val companyPattern = """\((.*),"(.*)"\)""".r
  val passengerPattern = """\((.*),"(.*)"\)""".r
  val tripPattern = """\(([0-9]{4}),([0-9]*?),"(.*?)","(.*?)","(.*?)","(.*?)","(.*?)"\)""".r
  val passInTripPattern = """\(([0-9]*?),"(.*?)",(.*?),"(.*?)"\)""".r
  def getCompany():List[Company] = for{
      line <- file.dropWhile("""-*Company-*""".r.unapplySeq(_).isDefined == false).tail.takeWhile(companyPattern.unapplySeq(_).isDefined)
      companyPattern(id,name) <- companyPattern.findAllIn(line)
    }yield Company(Option(id.toLong), name)

  def getPassenger():List[Passenger] =
    for{
      line <- file.dropWhile("""-*Passenger-*""".r.unapplySeq(_).isDefined == false).tail.takeWhile(passengerPattern.unapplySeq(_).isDefined)
      passengerPattern(id,name) <- passengerPattern.findAllIn(line)
    } yield Passenger(Option(id.toLong), name)

  def getTrip():List[Trip] =
    for{
      line <- file.dropWhile("""-*Trip-*""".r.unapplySeq(_).isDefined == false).tail.takeWhile(tripPattern.unapplySeq(_).isDefined)
      tripPattern(tripNo, companyId, plane, townFrom, townTo, timeOut, timeIn) <- tripPattern.findAllIn(line)
    } yield Trip(Option(tripNo.toLong), companyId.toLong, plane, townFrom, townTo, timeOut, timeIn)

  def getPassInTrip():List[PassInTrip] = for{
      line <- file.dropWhile("""-*Pass_in_trip-*""".r.unapplySeq(_).isDefined == false).tail.takeWhile(passInTripPattern.unapplySeq(_).isDefined)
      passInTripPattern(tripId, dateTo, passengerId, place) <- passInTripPattern.findAllIn(line)
    } yield PassInTrip(tripId.toLong, dateTo, passengerId.toLong, place)
  //file.dropWhile("""-*Pass_in_trip-*""".r.unapplySeq(_).isDefined == false).tail.takeWhile(passInTripPattern.unapplySeq(_).isDefined).map{ case passInTripPattern(tripId, dateTo, passengerId, place) => PassInTrip(Option(tripId.toLong), tripId.toLong, dateTo, passengerId.toLong, place)}.toSeq

}
