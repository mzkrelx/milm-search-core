package org.milmsearch.core.domain

/**
 * 絞り込み条件.
 */
case class Filter[E <: Enumeration](column: E#Value, value: Any)

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

  def toRange() = Range((page - 1) * count, count)
}

/**
 * ソート項目の列挙型
 */
trait SortByEnum extends Enumeration {}

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