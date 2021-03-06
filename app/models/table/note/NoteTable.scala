package models.table.note

import java.time.LocalDateTime
import java.util.UUID

import models.note.NoteRow
import com.kyleu.projectile.services.database.slick.SlickQueryService.imports._

object NoteTable {
  val query = TableQuery[NoteTable]

  def getByPrimaryKey(id: UUID) = query.filter(_.id === id).result.headOption
  def getByPrimaryKeySeq(ids: Seq[UUID]) = query.filter(_.id.inSet(ids)).result

  def removeByPrimaryKey(id: UUID) = query.filter(_.id === id).delete

  def insert(model: NoteRow) = query += model
  def insertBatch(seq: Seq[NoteRow]) = query ++= seq
}

class NoteTable(tag: Tag) extends Table[NoteRow](tag, "note") {
  def id = column[UUID]("id", O.PrimaryKey)
  def relType = column[Option[String]]("rel_type")
  def relPk = column[Option[String]]("rel_pk")
  def text = column[String]("text")
  def author = column[UUID]("author")
  def created = column[LocalDateTime]("created")

  override val * = (id, relType, relPk, text, author, created) <> ((NoteRow.apply _).tupled, NoteRow.unapply)
}
