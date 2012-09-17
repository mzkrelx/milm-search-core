package org.milmsearch.core.model.dao
import net.liftweb.mapper.LongKeyedMapper
import net.liftweb.mapper.CreatedTrait
import net.liftweb.mapper.IdPK
import net.liftweb.mapper.MappedString
import net.liftweb.mapper.MappedText
import net.liftweb.mapper.MappedEmail
import net.liftweb.mapper.MappedEnum
import net.liftweb.mapper.LongKeyedMetaMapper
import net.liftweb.mapper.CreatedUpdated
import org.milmsearch.core.model.service.MlProposalStatus
import org.milmsearch.core.model.service.MlArchiveType
import org.milmsearch.core.model.service.MlProposal

/**
 * ML登録申請情報 の DAO
 */
trait MlProposalDaoComponent {
  private[model] val mlProposalDao: MlProposalDao

  /**
   * DAO の実体
   */
  class MlProposalDao {
    def find(id: Long): Option[MlProposal] = None
  }
}

/**
 * O/R マッパー
 */
package mapper {

  /**
   * ML登録申請情報テーブルの操作を行う
   */
  private[dao] object MlProposal extends MlProposal
      with LongKeyedMetaMapper[MlProposal] {
    override def fieldOrder = createdAt :: Nil
  }
  
  /**
   * ML登録申請情報のモデルクラス
   */
  private[dao] class MlProposal extends LongKeyedMapper[MlProposal]
      with IdPK with CreatedUpdated {
    def getSingleton = MlProposal
  
    object proposerName extends MappedString(this, 200)
    object proposerEmail extends MappedEmail(this, 200)
    object mlTitle extends MappedString(this, 200)
    object status extends MappedEnum(this, MlProposalStatus)
    object archiveType extends MappedEnum(this, MlArchiveType)
    object archiveUrl extends MappedText(this)
    object comment extends MappedText(this)
  }

}