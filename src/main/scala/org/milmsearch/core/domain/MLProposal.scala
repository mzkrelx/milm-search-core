/*
 * MilmSearch is a mailing list searching system.
 *
 * Copyright (C) 2013 MilmSearch Project.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 *
 * You can contact MilmSearch Project at mailing list
 * milm-search-public@lists.sourceforge.jp.
 */
package org.milmsearch.core.domain
import java.net.URL
import java.util.Date
import org.milmsearch.common.RichEnumeration


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
  archiveType: Option[MLArchiveType.Value] = None,
  archiveURL: Option[URL] = None,
  comment: Option[String] = None,
  createdAt: Date,
  updatedAt: Date,
  judgedAt: Option[Date] = None,
  adminComment: Option[String] = None
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
  archiveType: Option[MLArchiveType.Value] = None,
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
  archiveType: MLArchiveType.Value,
  archiveURL: URL,
  adminComment: Option[String]
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
object MLArchiveType extends Enumeration {
  val Mailman = Value("mailman")
  val Other   = Value("other")
}

/**
 * ML登録申請の状態
 */
object MLProposalStatus extends RichEnumeration {
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
  val MLTitle       = Value("mlTitle")
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
  val MLTitle       = Value("mlTitle")
  val Status        = Value("status")
  val ArchiveType   = Value("archiveType")
  val ArchiveURL    = Value("archiveURL")
  val Comment       = Value("comment")
  val CreatedAt     = Value("createdAt")
  val UpdatedAt     = Value("updatedAt")
  val JudgedAt      = Value("judgedAt")
  val AdminComment  = Value("adminComment")
}
