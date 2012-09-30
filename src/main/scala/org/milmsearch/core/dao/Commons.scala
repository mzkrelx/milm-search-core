package org.milmsearch.core.dao

/**
 * 取得するデータの範囲
 */
case class Range(offset: Long, limit: Long)

/**
 * 取得するデータのソート方法
 */
case class Sort(column: Symbol, sortOrder: SortOrder.Value)

/**
 * ソート順序
 */
object SortOrder extends Enumeration {
  val Asc, Desc = Value
}