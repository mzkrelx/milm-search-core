package org.milmsearch.core.domain

import java.net.URL
import org.joda.time.DateTime

/**
 * ML情報
 *
 * @param id ID
 * @param title MLのタイトル
 * @param archiveType MLのアーカイブのタイプ
 * @param archiveUrl MLのアーカイブページのURL
 * @param lastMailedAt 最終投稿日時
 * @param approvedAt ML登録申請が承認された日時
 */
case class ML(
  id: Long,
  title: String,
  archiveType: MlArchiveType.Value,
  archiveURL: URL,
  lastMailedAt: DateTime,
  approvedAt: DateTime)

/**
 * ML情報の作成要求
 */
case class CreateMLRequest(
  title: String,
  archiveType: MlArchiveType.Value,
  archiveURL: URL,
  approvedAt: DateTime)

/**
 * ML情報の絞り込みに使える項目
 */
object MLFilterBy extends FilterByEnum {
  val Title = Value("title")
}

/**
 * ML情報の並べ替えに使える項目
 */
object MLSortBy extends SortByEnum {
  val Title        = Value("title")
  val LastMailedAt = Value("lastMailedAt")
}

/**
 * ML情報の検索結果
 */
case class MLSearchResult(
  totalResults: Long,
  startIndex: Long,
  itemsPerPage: Long,
  items: List[ML]) extends SearchResult[ML]