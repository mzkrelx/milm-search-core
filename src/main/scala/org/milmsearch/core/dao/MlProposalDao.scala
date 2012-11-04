package org.milmsearch.core.dao
import java.net.URL
import org.milmsearch.core.domain.CreateMlProposalRequest
import org.milmsearch.core.domain.Filter
import org.milmsearch.core.domain.MlArchiveType
import org.milmsearch.core.domain.MlProposal
import org.milmsearch.core.domain.{MlProposalStatus => MLPStatus}
import org.milmsearch.core.domain.Range
import org.milmsearch.core.domain.Sort
import net.liftweb.mapper.MappedField.mapToType
import net.liftweb.mapper.AscOrDesc
import net.liftweb.mapper.BaseOwnedMappedField
import net.liftweb.mapper.By
import net.liftweb.mapper.CreatedUpdated
import net.liftweb.mapper.IdPK
import net.liftweb.mapper.IdPK$id$
import net.liftweb.mapper.IdPK$id$
import net.liftweb.mapper.IndexItem
import net.liftweb.mapper.LongKeyedMapper
import net.liftweb.mapper.LongKeyedMetaMapper
import net.liftweb.mapper.MappedEmail
import net.liftweb.mapper.MappedEnum
import net.liftweb.mapper.MappedField
import net.liftweb.mapper.MappedString
import net.liftweb.mapper.MappedText
import net.liftweb.mapper.Mapper
import net.liftweb.mapper.MaxRows
import net.liftweb.mapper.OrderBy
import net.liftweb.mapper.StartAt
import mapper.{MlProposalField => MLPField}
import org.milmsearch.core.domain.{MlProposalFilterBy => MLPBy}
import mapper.{MlProposalMetaMapper => MLPMMapper}
import mapper.{MlProposalMapper => MLPMapper}

class NoSuchFieldException(msg: String) extends Exception(msg)
class UnexpectedValueException(msg: String) extends Exception(msg)

/**
 * ML登録申請情報 の DAO
 */
trait MlProposalDao {
  def findAll(range: Range, sort: Sort): List[MlProposal]
  def findAll[T](filter: Filter[MLPBy.type],
    range: Range, sort: Sort): List[MlProposal]
  def find(id: Long): Option[MlProposal]
  def create(request: CreateMlProposalRequest): Long
  def count(filter: Filter[MLPBy.type]): Long
  def count(): Long
}

/**
 * MlProposalDao の実装クラス
 */
class MlProposalDaoImpl extends MlProposalDao {
  def find(id: Long) = None
  def create(request: CreateMlProposalRequest) = 0L

  def findAll(range: Range, sort: Sort): List[MlProposal] = {
    mapper.MlProposalMetaMapper.findAll(
      StartAt(range.offset),
      MaxRows(range.limit),
      toOrderBy(sort)
    ) map { toDomain }   
  }
  
  def findAll[T](filter: Filter[MLPBy.type], range: Range, sort: Sort): List[MlProposal] = {
    mapper.MlProposalMetaMapper.findAll(
      toBy(filter),
      StartAt(range.offset),
      MaxRows(range.limit),
      toOrderBy(sort)
    ) map { toDomain }    
  }

  def count(filter: Filter[MLPBy.type]): Long = {
    mapper.MlProposalMetaMapper.count(toBy(filter))
  }
  
  def count(): Long = {
    mapper.MlProposalMetaMapper.count()
  }
  
  private def toDomain(mapper: org.milmsearch.core.dao.mapper.MlProposalMapper) = {
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
      mapper.updatedAt.get
    )
  }
  
  def toBy(filter: Filter[MLPBy.type]) = {
    filter match {
      case Filter(MLPBy.Status, v: MLPStatus.Value)
        => By(MLPMMapper.status, v)
      case _ => throw new NoSuchFieldException(
        "Can't convert Filter to By")
    }
  }
  
  def toOrderBy(sort: Sort) = {
    OrderBy(toMappedField(MLPField.withName(
        sort.column.name)), DaoHelper.toAscOrDesc(sort.sortOrder))
  }
  
  def toMappedField(field: MLPField.Value) = {
    mapper.MlProposalMetaMapper.fieldByName(field.toString()).open_!
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
      archiveType, archiveUrl, message, createdAt, updatedAt
    )
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
  
  private[dao] object MlProposalField extends Enumeration {
    val Id = Value("id")
    val ProposerName = Value("proposerName")
    val ProposerEmail = Value("proposerEmail")
    val MlTitle = Value("mlTitle")
    val Status = Value("status")
    val ArchiveType = Value("archiveType")
    val ArchiveUrl = Value("archiveUrl")
    val Comment = Value("message")
    val CreatedAt = Value("createdAt")
    val UpdatedAt = Value("updatedAt")
  }

}