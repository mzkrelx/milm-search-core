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
 * @param archiveURL MLのアーカイブページのURL
 * @param comment コメント
 * @param createdAt 作成日時
 * @param updatedAt 更新日時
 */
case class MLProposal(
  id: Long,
  proposerName: String,
  proposerEmail: String,
  mlTitle: String,
  status: MLProposalStatus.Value,
  archiveType: Option[MlArchiveType.Value] = None,
  archiveURL: Option[URL] = None,
  comment: Option[String] = None,
  createdAt: Date,
  updatedAt: Date,
  judgedAt: Option[Date] = None
  // TODO 管理者コメント追加
)

/**
 * ML登録申請の入力情報
 *
 * @param proposerName 申請者名
 * @param proposerEmail 申請者メールアドレス
 * @param mlTitle MLのタイトル
 * @param status 申請状況
 * @param archiveType MLのアーカイブのタイプ
 * @param archiveURL MLのアーカイブページのURL
 * @param comment コメント
 */
case class CreateMLProposalRequest(
  proposerName: String,
  proposerEmail: String,
  mlTitle: String,
  status: MLProposalStatus.Value,
  archiveType: Option[MlArchiveType.Value] = None,
  archiveURL: Option[URL] = None,
  comment: Option[String] = None
)


/**
 * ML登録申請の更新情報
 *
 * @param mlTitle MLのタイトル
 * @param archiveType MLのアーカイブのタイプ
 * @param archiveURL MLのアーカイブページのURL
 */
case class UpdateMLProposalRequest(
  mlTitle: String,
  archiveType: MlArchiveType.Value,
  archiveURL: URL
  // TODO 管理者コメント追加
)

/**
 * ML登録申請情報の検索結果
 */
case class MLProposalSearchResult(
  totalResults: Long,
  startIndex: Long,
  itemsPerPage: Long,
  mlProposals: List[MLProposal]
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
object MLProposalStatus extends Enumeration {
  val New      = Value("new")
  val Accepted = Value("accepted")
  val Rejected = Value("rejected")
}

/**
 * ML登録申請の絞り込みに使える項目
 */
object MLProposalFilterBy extends FilterByEnum {
  val Status = Value("status")
}

/**
 * ML登録申請の並べ替えに使える項目
 */
object MLProposalSortBy extends SortByEnum {
  val MlTitle       = Value("mlTitle")
  val Status        = Value("status")
  val ArchiveType   = Value("archiveType")
  val CreatedAt     = Value("createdAt")
  val UpdatedAt     = Value("updatedAt")
}

/**
 * ML登録申請の項目
 */
object MLProposalColumn extends Enumeration {
  val ProposerName  = Value("proposerName")
  val ProposerEmail = Value("proposerEmail")
  val MlTitle       = Value("mlTitle")
  val Status        = Value("status")
  val ArchiveType   = Value("archiveType")
  val ArchiveURL    = Value("archiveURL")
  val Comment       = Value("comment")
  val CreatedAt     = Value("createdAt")
  val UpdatedAt     = Value("updatedAt")
  val JudgedAt      = Value("judgedAt")
}
