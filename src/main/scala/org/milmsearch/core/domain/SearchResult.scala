package org.milmsearch.core.domain

/**
 * 検索結果
 */
trait SearchResult[T] {
  val totalResults: Long
  val startIndex: Long
  val itemsPerPage: Long
  val items: List[T]
}