package org.milmsearch.core.api
import org.milmsearch.core.domain.SortByEnum
import org.milmsearch.core.domain.SortOrder
import org.milmsearch.core.domain.Filter
import org.milmsearch.core.domain.FilterByEnum

/**
 * 一覧のときにページネーションするためのトレイト
 */
trait PageableResource {
  
  protected val defaultSortBy: SortByEnum#Value
  protected val defaultSortOrder = SortOrder.Ascending
  protected val defaultStartPage = 1L
  protected val defaultCount = 10L
  
  /** 1ページの項目数の上限値 */
  protected val maxCount = 100L
  
  /**
   * Filter オブジェクトを作る
   * 
   * @param filterBy
   * @param filterValue
   */
  protected def createFilter[E <: FilterByEnum](
      filterBy: Option[String],
      filterByENUM: E,
      filterValue: Option[String]): Option[Filter[E]] =
    (filterBy, filterValue) match {
      case (None, None) => None
      case (Some(by), Some(value)) =>
        try {
          Some(Filter(filterByENUM.withName(by), value))
        } catch {
          case e: NoSuchElementException =>
            throw new BadQueryParameterException(
              "Can't create filter. by[%s], value[%s]"
                format (by, value))
        }
      case _ => throw new BadQueryParameterException(
        "Invalid filter. Please query filterBy and " +
        "filterValue at the same time.")
    }

}