package org.milmsearch.core.api
import org.milmsearch.core.domain.SortByEnum
import org.milmsearch.core.domain.SortOrder

/**
 * 一覧のときにページネーションするためのトレイト
 */
trait PageableResource {
  
  protected val defaultSortBy: SortByEnum#Value
  protected val defaultSortOrder = SortOrder.Ascending
  protected val defaultStartPage = 1
  protected val defaultCount = 10
  
  /** 1ページの項目数の上限値 */
  protected val maxCount = 100
  
}