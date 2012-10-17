package org.milmsearch.core.dao
import org.milmsearch.core.domain.CreateMlProposalRequest
import org.milmsearch.core.domain.MlArchiveType
import org.milmsearch.core.domain.MlProposal
import org.milmsearch.core.domain.MlProposalStatus

import java.net.URL
import org.milmsearch.core.domain.Filter
import org.milmsearch.core.domain.Range
import org.milmsearch.core.domain.Sort
import org.milmsearch.core.domain.SortOrder
import net.liftweb.mapper.MappedField.mapToType
import net.liftweb.mapper.Ascending
import net.liftweb.mapper.BaseOwnedMappedField
import net.liftweb.mapper.By
import net.liftweb.mapper.CreatedUpdated
import net.liftweb.mapper.Descending
import net.liftweb.mapper.IdPK
import net.liftweb.mapper.IndexItem
import net.liftweb.mapper.LongKeyedMapper
import net.liftweb.mapper.LongKeyedMetaMapper
import net.liftweb.mapper.MappedEnum
import net.liftweb.mapper.MappedField
import net.liftweb.mapper.MappedString
import net.liftweb.mapper.MappedText
import net.liftweb.mapper.Mapper
import net.liftweb.mapper.MaxRows
import net.liftweb.mapper.OrderBy
import net.liftweb.mapper.StartAt
import net.liftweb.mapper.MappedEmail

class NoSuchFieldException(msg: String) extends Exception(msg)
class UnexpectedValueException(msg: String) extends Exception(msg)

/**
 * ML登録申請情報 の DAO
 */
trait MlProposalDao {
  def findAll(range: Range, sort: Sort): List[MlProposal]
  def findAll(filter: Filter, range: Range, sort: Sort): List[MlProposal]
  def find(id: Long): Option[MlProposal]
  def create(request: CreateMlProposalRequest): Long
  def count(filter: Filter): Long
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
  
  def findAll(filter: Filter, range: Range, sort: Sort): List[MlProposal] = {
    mapper.MlProposalMetaMapper.findAll(
      toBy(filter),
      StartAt(range.offset),
      MaxRows(range.limit),
      toOrderBy(sort)
    ) map { toDomain }    
  }

  def count(filter: Filter): Long = {
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
  
  def toBy(filter: Filter) = {
    By(toMappedField(filter.column), filter.value)
  }
  
  def toOrderBy(sort: Sort) = {
    OrderBy(toMappedField(sort.column), toAscOrDesc(sort.sortOrder))
  }
  
  def toMappedField(field: Symbol) = {
    field match {
      case mapper.MlProposalField.ProposerName => mapper.MlProposalMetaMapper.fieldByName(field.toString()).open_!
      case mapper.MlProposalField.ProposerEmail => mapper.MlProposalMetaMapper.fieldByName(field.toString()).open_!
      case mapper.MlProposalField.MlTitle => mapper.MlProposalMetaMapper.fieldByName(field.toString()).open_!
      case mapper.MlProposalField.Status => mapper.MlProposalMetaMapper.fieldByName(field.toString()).open_!
      case mapper.MlProposalField.ArchiveType => mapper.MlProposalMetaMapper.fieldByName(field.toString()).open_!
      case mapper.MlProposalField.ArchiveUrl => mapper.MlProposalMetaMapper.fieldByName(field.toString()).open_!
      case mapper.MlProposalField.Comment => mapper.MlProposalMetaMapper.fieldByName(field.toString()).open_!
      case s: Symbol => throw new NoSuchFieldException("Invalid field name symbol.[%s]" format s)
    }
  }
  
  def toAscOrDesc(order: SortOrder.Value) = {
    order match {
      case SortOrder.Ascending => Ascending
      case SortOrder.Descending => Descending
    }
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
    object status extends MappedEnum(this, MlProposalStatus)
    object archiveType extends MappedEnum(this, MlArchiveType)
    object archiveUrl extends MappedText(this)
    object message extends MappedText(this)
  }
  
  private[dao] object MlProposalField extends Enumeration {
    val ProposerName = Value("proposerName")
    val ProposerEmail = Value("proposerEmail")
    val MlTitle = Value("mlTitle")
    val Status = Value("status")
    val ArchiveType = Value("archiveType")
    val ArchiveUrl = Value("archiveUrl")
    val Comment = Value("comment")
  }

}