package org.milmsearch.core.domain
import java.net.URL
import java.util.Date

/**
 * ML登録申請情報
 * 
 * @param id ID
 * @param proposerName 申請者名
 * @param proposerEmail 申請者メールアドレス
 * @param mlTitle MLのタイトル
 * @param status 申請状況
 * @param archiveType MLのアーカイブのタイプ
 * @param archiveUrl MLのアーカイブページのURL
 * @param comment コメント
 * @param createdAt 作成日時
 * @param updatedAt 更新日時
 */
case class MlProposal(
  id: Long,
  proposerName: String,
  proposerEmail: String,
  mlTitle: String,
  status: MlProposalStatus.Value,
  archiveType: Option[MlArchiveType.Value] = None,
  archiveUrl: Option[URL] = None,
  comment: Option[String] = None,
  createdAt: Date,
  updatedAt: Date
)

/**
 * ML登録申請の入力情報
 * 
 * @param proposerName 申請者名
 * @param proposerEmail 申請者メールアドレス
 * @param mlTitle MLのタイトル
 * @param status 申請状況
 * @param archiveType MLのアーカイブのタイプ
 * @param archiveUrl MLのアーカイブページのURL
 * @param comment コメント
 */
case class CreateMlProposalRequest(
  proposerName: String,
  proposerEmail: String,
  mlTitle: String,
  status: MlProposalStatus.Value,
  archiveType: Option[MlArchiveType.Value] = None,
  archiveUrl: Option[URL] = None,
  comment: Option[String] = None
)

case class MlProposalSearchResult(
  totalResults: Long,
  startIndex: Long,
  itemsPerPage: Long,
  mlProposals: List[MlProposal]
)

/**
 * MLのアーカイブのタイプ(ソフトウェア)
 */
object MlArchiveType extends Enumeration {
  val Mailman = Value("mailman")
  val Other   = Value("other")
}

/**
 * ML登録申請の状態
 */
object MlProposalStatus extends Enumeration {
  val New      = Value("new")
  val Accepted = Value("accepted")
  val Rejected = Value("rejected")
}

/**
 * ML登録申請の絞り込みに使える項目
 */
object MlProposalFilterBy extends Enumeration {
  val Status = Value("status")
}

/**
 * ML登録申請の並べ替えに使える項目
 */
object MlProposalSortBy extends Enumeration {
  val Id            = Value("id")
  val ProposerName  = Value("proposerName")
  val ProposerEmail = Value("proposerEmail")
  val MlTitle       = Value("mlTitle")
  val Status        = Value("status")
  val ArchiveType   = Value("archiveType")
  val ArchiveUrl    = Value("archiveUrl")
  val CreatedAt     = Value("createdAt")
  val UpdatedAt     = Value("updatedAt")
}