package services.audit

import java.util.UUID

import models.audit.Audit
import models.queries.audit.AuditRecordQueries
import models.result.data.DataField
import models.result.filter.Filter
import models.result.orderBy.OrderBy
import services.ModelServiceHelper
import services.database.ApplicationDatabase
import util.FutureUtils.databaseContext
import util.tracing.{TraceData, TracingService}

@javax.inject.Singleton
class AuditRecordService @javax.inject.Inject() (override val tracing: TracingService) extends ModelServiceHelper[Audit.Record]("auditRecord") {
  def getByPrimaryKey(id: UUID)(implicit trace: TraceData) = {
    traceF("get.by.primary.key")(td => ApplicationDatabase.query(AuditRecordQueries.getByPrimaryKey(id))(td))
  }
  def getByPrimaryKeySeq(idSeq: Seq[UUID])(implicit trace: TraceData) = {
    traceF("get.by.primary.key.seq")(td => ApplicationDatabase.query(AuditRecordQueries.getByPrimaryKeySeq(idSeq))(td))
  }

  override def countAll(filters: Seq[Filter] = Nil)(implicit trace: TraceData) = {
    traceF("get.all.count")(td => ApplicationDatabase.query(AuditRecordQueries.countAll(filters))(td))
  }
  override def getAll(filters: Seq[Filter], orderBys: Seq[OrderBy], limit: Option[Int], offset: Option[Int] = None)(implicit trace: TraceData) = {
    traceF("get.all")(td => ApplicationDatabase.query(AuditRecordQueries.getAll(filters, orderBys, limit, offset))(td))
  }

  // Search
  override def searchCount(q: String, filters: Seq[Filter])(implicit trace: TraceData) = {
    traceF("search.count")(td => ApplicationDatabase.query(AuditRecordQueries.searchCount(q, filters))(td))
  }
  override def search(q: String, filters: Seq[Filter], orderBys: Seq[OrderBy], limit: Option[Int], offset: Option[Int] = None)(implicit trace: TraceData) = {
    traceF("search")(td => ApplicationDatabase.query(AuditRecordQueries.search(q, filters, orderBys, limit, offset))(td))
  }

  def searchExact(q: String, orderBys: Seq[OrderBy] = Nil, limit: Option[Int] = None, offset: Option[Int] = None)(implicit trace: TraceData) = {
    traceF("search.exact")(td => ApplicationDatabase.query(AuditRecordQueries.searchExact(q, orderBys, limit, offset))(td))
  }

  def countByAuditId(auditId: UUID)(implicit trace: TraceData) = traceF("count.by.auditId") { td =>
    ApplicationDatabase.query(AuditRecordQueries.CountByAuditId(auditId))(td)
  }
  def getByAuditId(auditId: UUID, orderBys: Seq[OrderBy], limit: Option[Int], offset: Option[Int])(implicit trace: TraceData) = {
    traceF("get.by.auditId")(td => ApplicationDatabase.query(AuditRecordQueries.GetByAuditId(auditId, orderBys, limit, offset))(td))
  }
  def getByAuditIdSeq(auditIdSeq: Seq[UUID])(implicit trace: TraceData) = traceF("get.by.auditId.seq") { td =>
    ApplicationDatabase.query(AuditRecordQueries.GetByAuditIdSeq(auditIdSeq))(td)
  }

  // Mutations
  def insert(model: Audit.Record)(implicit trace: TraceData) = {
    traceF("insert")(td => ApplicationDatabase.execute(AuditRecordQueries.insert(model))(td).flatMap {
      case 1 => getByPrimaryKey(model.id)(td)
      case x => throw new IllegalStateException("Unable to find newly-inserted Audit Record.")
    })
  }
  def create(fields: Seq[DataField])(implicit trace: TraceData) = {
    traceF("create")(td => ApplicationDatabase.execute(AuditRecordQueries.create(fields))(td)).map { _ =>
      None: Option[Audit.Record]
    }
  }

  def remove(id: UUID)(implicit trace: TraceData) = {
    traceF("remove")(td => ApplicationDatabase.query(AuditRecordQueries.getByPrimaryKey(id))(td).flatMap {
      case Some(current) => ApplicationDatabase.execute(AuditRecordQueries.removeByPrimaryKey(id))(td).map(_ => current)
      case None => throw new IllegalStateException(s"Cannot find Note matching [$id].")
    })
  }

  def update(id: UUID, fields: Seq[DataField])(implicit trace: TraceData) = {
    traceF("update")(td => ApplicationDatabase.query(AuditRecordQueries.getByPrimaryKey(id))(td).flatMap {
      case Some(current) => ApplicationDatabase.execute(AuditRecordQueries.update(id, fields))(td).flatMap { _ =>
        ApplicationDatabase.query(AuditRecordQueries.getByPrimaryKey(id))(td).map {
          case Some(newModel) => newModel
          case None => throw new IllegalStateException(s"Cannot find AuditRecord matching [$id].")
        }
      }
      case None => throw new IllegalStateException(s"Cannot find AuditRecord matching [$id].")
    })
  }

  def csvFor(operation: String, totalCount: Int, rows: Seq[Audit.Record])(implicit trace: TraceData) = {
    traceB("export.csv")(td => util.CsvUtils.csvFor(Some(key), totalCount, rows, AuditRecordQueries.fields)(td))
  }
}