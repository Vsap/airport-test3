package model

import slick.lifted.Tag
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Future

case class Company(id: Option[Long], name: String)

class CompanyTable(tag: Tag) extends Table[Company](tag, "companies"){
  val id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  val name = column[String]("name")

  def * = (id.?, name) <> (Company.apply _ tupled, Company.unapply)
}

object CompanyTable{
  val table = TableQuery[CompanyTable]
}

