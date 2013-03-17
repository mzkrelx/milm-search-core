package org.milmsearch.core.dao

import java.net.URL
import scala.collection.mutable.ListBuffer
import org.milmsearch.core.ComponentRegistry.{dateTimeService => Time}
import org.milmsearch.core.domain.CreateMLProposalRequest
import org.milmsearch.core.domain.Filter
import org.milmsearch.core.domain.MLArchiveType
import org.milmsearch.core.domain.MLProposal
import org.milmsearch.core.domain.MLProposalColumn
import org.milmsearch.core.domain.{MLProposalFilterBy => MLPFilterBy}
import org.milmsearch.core.domain.{MLProposalSortBy => MLPSortBy}
import org.milmsearch.core.domain.{MLProposalStatus => MLPStatus}
import org.milmsearch.core.domain.Range
import org.milmsearch.core.domain.Sort
import mapper.{MLProposalMapper => MLPMapper}
import mapper.{MLProposalMetaMapper => MLPMMapper}
import net.liftweb.common.Box
import net.liftweb.common.Empty
import net.liftweb.common.Failure
import net.liftweb.common.Full
import net.liftweb.common.Loggable
import net.liftweb.mapper.By
import net.liftweb.mapper.CreatedUpdated
import net.liftweb.mapper.IdPK
import net.liftweb.mapper.LongKeyedMapper
import net.liftweb.mapper.LongKeyedMetaMapper
import net.liftweb.mapper.MappedDateTime
import net.liftweb.mapper.MappedEmail
import net.liftweb.mapper.MappedEnum
import net.liftweb.mapper.MappedString
import net.liftweb.mapper.MappedText
import net.liftweb.mapper.MaxRows
import net.liftweb.mapper.OrderBy
import net.liftweb.mapper.QueryParam
import net.liftweb.mapper.StartAt
import java.util.Date

/**
 * ML登録申請情報 の DAO
 */
trait MLProposalDao {

  /**
   * 検索条件と取得範囲と並び順を指定して、ML登録申請情報を検索します。
   *
   * @param filter 検索条件
   * @param range  取得範囲
   * @param sort   並び順
   * @return List[MLProposal] ML登録申請情報のリスト
   */
  def findAll(range: Range,
    sort: Option[Sort[MLPSortBy.type]] = None,
    filter: Option[Filter[MLPFilterBy.type]] = None): List[MLProposal]

  def find(id: Long): Option[MLProposal]
  def create(request: CreateMLProposalRequest): Long

  /**
   * IDを指定して、ML登録申請情報を削除します。
   *
   * @param id: ID
   * @return Boolean  ??
   */
  def delete(id: Long): Boolean

  /**
   * 検索条件を指定して、件数を数えます。
   *
   * @param filter 検索条件
   * @return Long 件数
   */
  def count(filter: Option[Filter[MLPFilterBy.type]] = None): Long

  def update(id: Long, colVal: Pair[MLProposalColumn.Value, Object]): Boolean

  def update(id: Long, colValList: List[Pair[MLProposalColumn.Value, Object]]): Boolean

}

/**
 * MLProposalDao の実装クラス
 */
class MLProposalDaoImpl extends MLProposalDao with Loggable {

  def find(id: Long) = {
    MLPMMapper.find(id) match {
      case Empty => None
      case Full(mapper) => Some(toDomain(mapper))
      case Failure(message, e, _) => {
        logger.error(message, e)
        throw new DataAccessException("Failed to retrieve the data.")
      }
    }
  }

  def create(request: CreateMLProposalRequest) = toMapper(request).saveMe().id

  /**
   * ML登録申請情報ドメインを Mapper オブジェクトに変換する
   */
  private def toMapper(request: CreateMLProposalRequest): MLPMapper = {
    val now = Time().now().toDate
    MLPMMapper.create
      .proposerName(request.proposerName)
      .proposerEmail(request.proposerEmail)
      .mlTitle(request.mlTitle)
      .status(request.status.toString)
      .archiveType(request.archiveType map { _.toString } getOrElse null)
      .archiveURL(request.archiveURL map { _.toString } getOrElse null)
      .message(request.comment getOrElse null)
      .createdAt(now)
      .updatedAt(now)
      .judgedAt(null)
      .adminComment(null)
  }

  def findAll(range: Range,
    sort: Option[Sort[MLPSortBy.type]] = None,
    filter: Option[Filter[MLPFilterBy.type]] = None) = {
    val queryParams = ListBuffer[QueryParam[MLPMapper]](
      StartAt(range.offset),
      MaxRows(range.limit))
    if (sort.isDefined) {
      queryParams += toOrderBy(sort.get)
    }
    if (filter.isDefined) {
      queryParams += toBy(filter.get)
    }

    MLPMMapper.findAll(queryParams: _*) map toDomain
  }

  def count(filter: Option[Filter[MLPFilterBy.type]] = None) =
    if (filter.isDefined)
      MLPMMapper.count(toBy(filter.get))
    else
      MLPMMapper.count()

  private def toDomain(mapper: MLPMapper) =
    MLProposal(
      mapper.id.get,
      mapper.proposerName.get,
      mapper.proposerEmail.get,
      mapper.mlTitle.get,
      MLPStatus.withName(mapper.status.get),
      Option(MLArchiveType.withName(mapper.archiveType.get)),
      Option(new URL(mapper.archiveURL.get)),
      Option(mapper.message.get),
      mapper.createdAt.get,
      mapper.updatedAt.get,
      Option(mapper.judgedAt.get),
      Option(mapper.adminComment.get))

  /**
   * 指定された情報のmapperを返す
   * @param id ID
   * @return Option[MLPMapper]
   * @throws FindException
   */
  private def findMapper(id: Long):Option[MLPMapper] =
    MLPMMapper.find(id) match {
      case Full(mapper) => Some(mapper)
      case Empty => None
      case Failure(message, e, _) => throw new FindException(message)
    }

  def toBy(filter: Filter[MLPFilterBy.type]) = filter match {
    case Filter(MLPFilterBy.Status, v: MLPStatus.Value) =>
      By(MLPMMapper.status, v.toString)
    case _ => throw new NoSuchFieldException(
      "Can't convert Filter to By")
  }

  def toOrderBy(sort: Sort[MLPSortBy.type]) = {
    import MLPSortBy._
    import MLPMMapper._
    OrderBy(sort.column match {
      case MLTitle => mlTitle
      case Status => status
      case ArchiveType => archiveType
      case CreatedAt => createdAt
      case UpdatedAt => updatedAt
      case _ => throw new NoSuchFieldException(
        "Can't convert Filter to By")
    }, DaoHelper.toAscOrDesc(sort.sortOrder))
  }

  def delete(id: Long): Boolean = {
    val mapper = findMapper(id)
    mapper match {
      case Some(m) => MLPMMapper.delete_!(m)
      case None => false
    }
  }

  def update(id: Long, colVal: Pair[MLProposalColumn.Value, Object]) = {
    findMapper(id) match {
      case None => false
      case Some(mlpMapper) => {
        setMLPMapper(mlpMapper, colVal)
        mlpMapper.save()
      }
    }
  }

  def update(id: Long, colValList: List[Pair[MLProposalColumn.Value, Object]]) = {
    findMapper(id) match {
      case None => false
      case Some(mlpMapper) => {
        import MLProposalColumn._
        import mlpMapper._
        colValList foreach { colVal => setMLPMapper(mlpMapper, colVal) }
        mlpMapper.save()
      }
    }
  }

  private def setMLPMapper(mlpMapper: MLPMapper, colVal: Pair[MLProposalColumn.Value, Object]) {
    import MLProposalColumn._
    import mlpMapper._

    colVal match {
      case (ProposerName,  value: String) => proposerName.set(value)
      case (ProposerEmail, value: String) => proposerEmail.set(value)
      case (MLTitle,       value: String) => mlTitle.set(value)
      case (Status,        value: String) => status.set(value)
      case (ArchiveType,   value: String) => archiveType.set(value)
      case (ArchiveURL,    value: String) => archiveURL.set(value)
      case (Comment,       value: String) => message.set(value)
      case (CreatedAt,     value: Date)   => createdAt.set(value)
      case (UpdatedAt,     value: Date)   => updatedAt.set(value)
      case (JudgedAt,      value: Date)   => judgedAt.set(value)
      case (AdminComment,  value: String) => adminComment.set(value)
      case notMLPColumn => throw new NoSuchFieldException(
        "Can't update by [%s]." formatted (notMLPColumn.toString))
    }
    mlpMapper
  }
}

/**
 * O/R マッパー
 */
package mapper {

  /**
   * ML登録申請情報テーブルの操作を行う
   */
  private[dao] object MLProposalMetaMapper
    extends MLProposalMapper
    with LongKeyedMetaMapper[MLProposalMapper] {
    override def dbTableName = "ml_proposal"
    override def fieldOrder = List(
      id, proposerName, proposerEmail, mlTitle, status,
      archiveType, archiveURL, message, createdAt, updatedAt,
      judgedAt, adminComment)
  }

  /**
   * ML登録申請情報のモデルクラス
   */
  private[dao] class MLProposalMapper extends
      LongKeyedMapper[MLProposalMapper] with IdPK {
    def getSingleton = MLProposalMetaMapper

    // DB定義（S.N.)
    object proposerName extends MappedString(this, 200)
    object proposerEmail extends MappedEmail(this, 200)
    object mlTitle extends MappedString(this, 200)
    object status extends MappedString(this, 200)
    object archiveType extends MappedString(this, 200)
    object archiveURL extends MappedText(this)
    object message extends MappedText(this)
    object createdAt extends MappedDateTime(this)
    object updatedAt extends MappedDateTime(this)
    object judgedAt extends MappedDateTime(this)
    object adminComment extends MappedText(this)
  }
}
