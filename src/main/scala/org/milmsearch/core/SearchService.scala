package org.milmsearch.core
import org.apache.lucene.document.Document

/**
 * 検索結果情果情報のケースクラス
 */
case class SearchResultScala(
  emails: List[Email],
  total: Int
)

/**
 * 検索するメールの項目の列挙型
 */
object SearchFieldScala extends Enumeration {
  // TODO UIの検索画面の仕様による
  val Subject, Text = Value
}

/**
 * 検索結果の順序を指定する項目
 */
object SortItem extends Enumeration {
  // TODO UIの検索画面の仕様による
  val Date, From = Value
}

/**
 * 検索結果の順序の並び順
 */
object SortOrder extends Enumeration {
  val Ascending, Descending = Value
}

/**
 * 検索条件のケースクラス
 */
case class SerchCondition(
  fields: List[SearchFieldScala.Value],
  query: String,
  itemCountPerPage: Int = 20,
  pageNumber: Int = 1,
  sort: (SortItem.Value, SortOrder.Value) = 
    (SortItem.Date, SortOrder.Ascending)
)

/**
 * Luceneからメールを検索するクラス
 */
object SearchService {

  def search(condition: SearchCondition): SearchResultScala = {
    // TODO
    SearchResultScala(Nil, 0)
  }

  def findEmailContent(scoreDoc: Int): String = {
    // TODO
    ""
  }

  private def toEmail(doc: Document): Email = {
    // TODO
    null
  }

  private def makeResult(Emails: List[Email],
      total: Int): SearchResultScala = {
    // TODO
    SearchResultScala(Nil, 0)
  }
}