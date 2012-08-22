package org.milmsearch.core

import java.io.File
import org.slf4j.LoggerFactory
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.queryParser.QueryParser
import org.apache.lucene.search.SortField
import org.apache.lucene.search.Sort
import org.apache.lucene.document.Document
import org.apache.lucene.store.FSDirectory
import org.apache.lucene.analysis.cjk.CJKAnalyzer
import org.apache.lucene.util.Version
import org.apache.lucene.search.Query

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
  val Subject = Value("subject")
  val Text = Value("text")
}

/**
 * 検索結果の順序を指定する項目
 */
object SortItem extends Enumeration {
  // TODO UIの検索画面の仕様による
  val Date = Value("date")
  val From = Value("from")
  
  def getType(sortItem: SortItem.Value): Int = {
    sortItem match {
      case Date => SortField.LONG
      case From => SortField.STRING_VAL
    }
  } 
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
case class SearchConditionScala(
  // TODO QueryParser でフィールドが1つしか指定できないので、とりあえずリストでなく
  // 1つのフィールドで。
  field: SearchFieldScala.Value,
  query: String,
  itemCountPerPage: Int = 20,
  pageNumber: Int = 1,
  sort: (SortItem.Value, SortOrder.Value) = 
    (SortItem.Date, SortOrder.Ascending)
)

/**
 * Luceneからメールを検索するクラス
 */
object SearchServiceScala {

  /** ロガー */
  private val logger = LoggerFactory.getLogger(getClass)
  
  def search(condition: SearchConditionScala): SearchResultScala = {
    val query = new QueryParser(
      Version.LUCENE_29, 
      condition.field.toString, 
      new CJKAnalyzer(Version.LUCENE_29)
    ).parse(condition.query)
    
    // TODO もっと本質的に!
    val searcher = new IndexSearcher(
      FSDirectory.open(
      new File(SystemConfig.getIndexDir())), true)
            
    val sort = new Sort(toSortField(condition.sort._1, condition.sort._2))
    
    val topDocs = searcher.search(query, null, 
      condition.itemCountPerPage * condition.pageNumber, sort)
    
    val emails = topDocs.scoreDocs.slice(
      condition.pageNumber - 1, 
      condition.itemCountPerPage * condition.pageNumber
    ) map { scoreDoc =>
      toEmail(scoreDoc.doc, searcher.doc(scoreDoc.doc))
    } toList
    
    SearchResultScala(emails, topDocs.totalHits)
  }

  private def toSortField(item: SortItem.Value,
    order: SortOrder.Value) = new SortField(
      item.toString, 
      SortItem.getType(item),
      order == SortOrder.Descending)

  def findEmailContent(scoreDoc: Int): String = {
    // TODO
    ""
  }

  private def toEmail(id: Int, doc: Document): Email = {
    // TODO
    null
  }

  private def makeResult(Emails: List[Email],
      total: Int): SearchResultScala = {
    // TODO
    SearchResultScala(Nil, 0)
  }
}