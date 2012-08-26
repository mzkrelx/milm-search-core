package org.milmsearch.core.mapper
import net.liftweb.mapper.LongKeyedMapper
import net.liftweb.mapper.CreatedTrait
import net.liftweb.mapper.IdPK
import net.liftweb.mapper.MappedString
import net.liftweb.mapper.MappedText
import net.liftweb.mapper.MappedEmail
import net.liftweb.mapper.MappedEnum
import org.milmsearch.core.sorg.milmsearch.coreervice.MlArchiveType
import net.liftweb.mapper.LongKeyedMetaMapper
import net.liftweb.mapper.CreatedUpdated
import org.milmsearch.core.sorg.milmsearch.coreervice.MlProposalStatus

/**
 * ML登録申請情報テーブルの操作を行う
 */
object MlProposalMapper extends MlProposalMapper
    with LongKeyedMetaMapper[MlProposalMapper] {
  override def fieldOrder = createdAt :: Nil
}

/**
 * ML登録申請情報のモデルクラス
 */
class MlProposalMapper extends LongKeyedMapper[MlProposalMapper]
    with IdPK with CreatedUpdated {
  def getSingleton = MlProposalMapper
  object proposerName extends MappedString(this, 200)
  object proposerEmail extends MappedEmail(this, 200)
  object mlTitle extends MappedString(this, 200)
  object status extends MappedEnum(this, MlProposalStatus)
  object archiveType extends MappedEnum(this, MlArchiveType)
  object archiveUrl extends MappedString(this, 200)
  object comment extends MappedText(this)
}
