package org.milmsearch.core.dao
import org.milmsearch.core.domain.MlArchiveType
import org.milmsearch.core.domain.MlProposal
import org.milmsearch.core.domain.MlProposalStatus
import net.liftweb.mapper.CreatedUpdated
import net.liftweb.mapper.IdPK
import net.liftweb.mapper.LongKeyedMapper
import net.liftweb.mapper.LongKeyedMetaMapper
import net.liftweb.mapper.MappedEmail
import net.liftweb.mapper.MappedEnum
import net.liftweb.mapper.MappedText
import net.liftweb.mapper.MappedString

/**
 * ML登録申請情報 の DAO
 */
trait MlProposalDao {
  def find(id: Long): Option[MlProposal]
  def create(mlProposal: MlProposal): Long
}

/**
 * MlProposalDao の実装クラス
 */
class MlProposalDaoImpl extends MlProposalDao {
  def find(id: Long) = None
  def create(mlProposal: MlProposal) = 0L
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

}