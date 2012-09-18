package org.milmsearch.core.domain
import java.net.URL

/**
 * ML登録申請情報
 * 
 * @param proposerName 申請者名
 * @param proposerEmail 申請者メールアドレス
 * @param mlTitle MLのタイトル
 * @param status 申請状況
 * @param archiveType MLのアーカイブのタイプ
 * @param archiveUrl MLのアーカイブページのURL
 * @param comment コメント
 */
case class MlProposal(
  proposerName: String,
  proposerEmail: String,
  mlTitle: String,
  status: MlProposalStatus.Value,
  archiveType: Option[MlArchiveType.Value] = None,
  archiveUrl: Option[URL] = None,
  comment: Option[String] = None
)

/**
 * MLのアーカイブのタイプ(ソフトウェア)
 */
object MlArchiveType extends Enumeration {
  val Mailman = Value
}

/**
 * ML登録申請の状態
 */
object MlProposalStatus extends Enumeration {
  val New, Accepted, Rejected = Value
}
