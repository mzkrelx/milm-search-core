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

/**
 * 取得するデータの範囲
 */
case class Range(offset: Long, limit: Long)

/**
 * 取得するデータの範囲
 */
case class Page(page: Long, count: Long) {
  require(page  > 0, "page must be positive number")
  require(count > 0, "count must be positive number")

  def toRange = Range((page - 1) * count, count)

  def getStartIndex = toRange.offset + 1
}

/**
 * 絞り込み条件の列挙型
 */
trait FilterByEnum extends Enumeration

/**
 * 絞り込み条件
 */
case class Filter[E <: FilterByEnum](column: E#Value, value: Any)

/**
 * ソート項目の列挙型
 */
trait SortByEnum extends Enumeration

/**
 * 取得するデータのソート方法
 */
case class Sort[E <: SortByEnum](column: E#Value, sortOrder: SortOrder.Value)

/**
 * ソート順序
 */
object SortOrder extends Enumeration {
  val Ascending = Value("ascending")
  val Descending = Value("descending")
}