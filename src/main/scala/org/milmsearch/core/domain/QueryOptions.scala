package org.milmsearch.core.domain

/**
 * 絞り込み条件.
 */
case class Filter[ENUM <: Enumeration](column: ENUM#Value, value: Any)

/**
 * 取得するデータの範囲
 */
case class Range(offset: Long, limit: Long)

/**
 * 取得するデータの範囲
 */
case class Page(page: Long, count: Long) {
  def toRange(): Range = {
    Range(if (page == 1) 0 else (page - 1) * count, count)
  }
}

/**
 * 取得するデータのソート方法
 */
case class Sort[ENUM <: Enumeration](column: ENUM#Value, sortOrder: SortOrder.Value)

/**
 * ソート順序
 */
object SortOrder extends Enumeration {
  val Ascending = Value("ascending")
  val Descending = Value("descending")
}