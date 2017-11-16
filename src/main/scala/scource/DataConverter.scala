package scource
import model._

import scala.io.Source

case class DataConverter(path: String) {
  val source: List[String] = Source.fromFile(path, "UTF-8").getLines.toList

  def getCompany(file: List[String]):List[Company] = {
    val companyPattern = """([0-9]+) ([a-z]+)""".r
    for{
      line <- file
      companyPattern(id,name) <- companyPattern.findAllIn(line)
    } yield Company(Option(id.toLong), name)
  }
  def getPassenger(file: List[String]):List[Passenger] = {
    val companyPattern = """([0-9]+)+\s+([A-Z+a-z+ \s +A-Z +a-z]+)""".r
    for{
      line <- file
      companyPattern(id,name) <- companyPattern.findAllIn(line)
    } yield Passenger(Option(id.toLong), name)
  }
  def getTrip(file: List[String]):List[Trip] = {
    val tripPattern = """([0-9]+) ([0-9]+) ([0-9]+) + \s + ([A-Z+a-z+ \s +A-Z +a-z]+)""".r
    for{
      line <- file
      tripPattern(id, companyId, plane, townFrom, townTo, name) <- tripPattern.findAllIn(line)
    } yield Trip(Option(id.toLong),companyId, plane, townFrom, townTo, name)
  }

}
