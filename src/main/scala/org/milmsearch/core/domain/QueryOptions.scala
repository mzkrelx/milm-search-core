package org.milmsearch.core.domain

/**
 * 絞り込み条件
 */
case class Filter(column: Symbol, value: String)

/**
 * 取得するデータの範囲
 */
case class Range(offset: Long, limit: Long)

/**
 * 取得するデータの範囲
 */
case class Page(page: Long, count: Long) {
  def toRange(): Range = {
    Range(page - 1 * count, count)
  }
}

/**
 * 取得するデータのソート方法
 */
case class Sort(column: Symbol, sortOrder: SortOrder.Value)

/**
 * ソート順序
 */
object SortOrder extends Enumeration {
  val Ascending = Value("ascending")
  val Descending = Value("descending")
}