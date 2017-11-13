package model

import slick.lifted.Tag
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Future

case class Passenger(id:Option[Long], name: String)

class PassengerTable(tag: Tag) extends Table[Passenger](tag, "passengers"){
  val id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  val name = column[String]("name")
  def * = (id.?, name) <> (Passenger.apply _ tupled, Passenger.unapply)
}

object PassengerTable{
  val table = TableQuery[PassengerTable]
}