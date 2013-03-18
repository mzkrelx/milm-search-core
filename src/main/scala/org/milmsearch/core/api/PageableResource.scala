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
package org.milmsearch.core.api
import org.milmsearch.core.domain.SortByEnum
import org.milmsearch.core.domain.SortOrder
import org.milmsearch.core.domain.Filter
import org.milmsearch.core.domain.FilterByEnum
import org.milmsearch.core.domain.Page

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

  def createPage(startPage: Long, count: Long, maxCount: Long = maxCount) = {
    if (startPage <= 0)
      throw new BadQueryParameterException(
        "Invalid startPage value. [%d]" format startPage)
    if (count <= 0 | count > maxCount)
      throw new BadQueryParameterException(
        "Invalid count value. [%d]" format count)
    Page(startPage, count)
  }

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