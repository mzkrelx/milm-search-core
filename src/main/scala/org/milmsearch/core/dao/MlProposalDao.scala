package org.milmsearch.core.dao
import java.net.URL
import org.milmsearch.core.domain.CreateMlProposalRequest
import org.milmsearch.core.domain.MlProposal
import org.milmsearch.core.domain.Filter
import org.milmsearch.core.domain.Range
import org.milmsearch.core.domain.Sort
import org.milmsearch.core.domain.MlArchiveType
import org.milmsearch.core.domain.{MlProposalSortBy => MLPSortBy}
import org.milmsearch.core.domain.{MlProposalFilterBy => MLPFilterBy}
import org.milmsearch.core.domain.{MlProposalStatus => MLPStatus}
import net.liftweb.mapper.MappedEnum
import net.liftweb.mapper.MappedString
import net.liftweb.mapper.MappedText
import net.liftweb.mapper.LongKeyedMapper
import net.liftweb.mapper.OrderBy
import net.liftweb.mapper.LongKeyedMetaMapper
import net.liftweb.mapper.MappedEmail
import net.liftweb.mapper.IdPK
import net.liftweb.mapper.CreatedUpdated
import net.liftweb.mapper.By
import net.liftweb.mapper.StartAt
import net.liftweb.mapper.MaxRows
import mapper.{MlProposalMetaMapper => MLPMMapper}
import mapper.{MlProposalMapper => MLPMapper}
import scala.collection.mutable.ListBuffer
import net.liftweb.mapper.QueryParam

/**
 * ML登録申請情報 の DAO
 */
trait MlProposalDao {

  /** 検索条件と取得範囲と並び順を指定して、ML登録申請情報を検索します。
   *
   * @param filter 検索条件
   * @param range  取得範囲
   * @param sort   並び順
   * @return List[MlProposal] ML登録申請情報のリスト
   */
  def findAll(filter: Option[Filter[MLPFilterBy.type]],
      range: Range,
      sort:   Option[Sort[MLPSortBy.type]]): List[MlProposal]

  def find(id: Long): Option[MlProposal]
  def create(request: CreateMlProposalRequest): Long

  /** 検索条件を指定して、件数を数えます。
   *
   * @param filter 検索条件
   * @return Long 件数
   */
  def count(filter: Option[Filter[MLPFilterBy.type]]): Long
}

/**
 * MlProposalDao の実装クラス
 */
class MlProposalDaoImpl extends MlProposalDao {
  def find(id: Long) = None
  def create(request: CreateMlProposalRequest) = 0L

  def findAll(filter: Option[Filter[MLPFilterBy.type]],
      range: Range,
      sort:  Option[Sort[MLPSortBy.type]]): List[MlProposal] = {
    val queryParams = ListBuffer[QueryParam[MLPMapper]](
      StartAt(range.offset),
      MaxRows(range.limit))
    if (sort.isDefined)   queryParams += toOrderBy(sort.get)
    if (filter.isDefined) queryParams += toBy(filter.get)

    MLPMMapper.findAll(queryParams: _*) map toDomain
  }

  def count(filter: Option[Filter[MLPFilterBy.type]]): Long =
    if (filter.isDefined) MLPMMapper.count(toBy(filter.get))
    else MLPMMapper.count()

  private def toDomain(mapper: MLPMapper) =
    MlProposal(
      mapper.id.get,
      mapper.proposerName.get,
      mapper.proposerEmail.get,
      mapper.mlTitle.get,
      mapper.status.get,
      Option(mapper.archiveType.get),
      Option(new URL(mapper.archiveUrl.get)),
      Option(mapper.message.get),
      mapper.createdAt.get,
      mapper.updatedAt.get)

  def toBy(filter: Filter[MLPFilterBy.type]) = filter match {
    case Filter(MLPFilterBy.Status, v: MLPStatus.Value) =>
      By(MLPMMapper.status, v)
    case _ => throw new NoSuchFieldException(
      "Can't convert Filter to By")
  }

  def toOrderBy(sort: Sort[MLPSortBy.type]) = {
    import MLPSortBy._
    import MLPMMapper._
    OrderBy(sort.column match {
      case MlTitle       => mlTitle
      case Status        => status
      case ArchiveType   => archiveType
      case CreatedAt     => createdAt
      case UpdatedAt     => updatedAt
      case _ => throw new NoSuchFieldException(
        "Can't convert Filter to By")
    }, DaoHelper.toAscOrDesc(sort.sortOrder))
  }

}

/**
 * O/R マッパー
 */
package mapper {

  /**
   * ML登録申請情報テーブルの操作を行う
   */
  private[dao] object MlProposalMetaMapper extends MlProposalMapper
      with LongKeyedMetaMapper[MlProposalMapper] {
    override def dbTableName = "ml_proposal"
    override def fieldOrder = List(
      id, proposerName, proposerEmail, mlTitle, status,
      archiveType, archiveUrl, message, createdAt, updatedAt)
  }

  /**
   * ML登録申請情報のモデルクラス
   */
  private[dao] class MlProposalMapper extends LongKeyedMapper[MlProposalMapper]
      with IdPK with CreatedUpdated {
    def getSingleton = MlProposalMetaMapper

    object proposerName extends MappedString(this, 200)
    object proposerEmail extends MappedEmail(this, 200)
    object mlTitle extends MappedString(this, 200)
    object status extends MappedEnum(this, MLPStatus)
    object archiveType extends MappedEnum(this, MlArchiveType)
    object archiveUrl extends MappedText(this)
    object message extends MappedText(this)
  }

}