package org.milmsearch.core.dao

import net.liftweb.common.Loggable
import net.liftweb.mapper.IdPK
import net.liftweb.mapper.LongKeyedMapper
import net.liftweb.mapper.LongKeyedMetaMapper
import net.liftweb.mapper.MappedString
import net.liftweb.mapper.MappedText
import net.liftweb.mapper.MappedDateTime
import org.milmsearch.core.domain.CreateMLRequest
import org.milmsearch.core.domain.ML
import org.milmsearch.core.domain.MLArchiveType
import mapper._
import net.liftweb.common.Full
import net.liftweb.common.Empty
import net.liftweb.common.Failure
import java.net.URL
import org.joda.time.DateTime
import org.milmsearch.core.domain.Filter
import org.milmsearch.core.domain.Sort
import org.milmsearch.core.domain.MLSortBy
import org.milmsearch.core.domain.MLFilterBy
import org.milmsearch.core.domain.Range
import net.liftweb.mapper.By
import net.liftweb.mapper.QueryParam
import net.liftweb.mapper.StartAt
import net.liftweb.mapper.MaxRows
import scala.collection.mutable.ListBuffer
import net.liftweb.mapper.OrderBy
import DaoHelper.toAscOrDesc
import net.liftweb.mapper.MappedField

/**
 * ML情報 の DAO
 */
trait MLDao {
  /**
   * ML情報を永続化する
   * 
   * @param req ML情報作成要求
   */
  def create(req: CreateMLRequest): Long
  
  /**
   * ML情報を検索する
   * 
   * @param id ML情報ID
   */
  def find(id: Long): Option[ML]
  
  /**
   * ML情報を検索する
   * 
   * @param sort ソート方法
   * @param filter 検索条件
   * @return 検索したML情報の一覧
   */
  def findAll(range: Range,
    sort: Option[Sort[MLSortBy.type]] = None,
    filter: Option[Filter[MLFilterBy.type]] = None): List[ML]
  
  /**
   * ML情報の件数を数える
   *
   * @param filter 検索条件
   * @return Long 件数
   */
  def count(filter: Option[Filter[MLFilterBy.type]] = None): Long
}

/**
 * MLDao の実装クラス
 */
class MLDaoImpl extends MLDao with Loggable {
  
  def create(req: CreateMLRequest) = toMapper(req).saveMe().id
  
  def find(id: Long) = MLMetaMapper.findByKey(id) match {
    case Empty   => None
    case Full(m) => Some(toDomain(m))
    case Failure(msg, e, _) =>
      throw e openOr new DataAccessException(msg)
  }
  
  def findAll(
      range: Range,
      sort: Option[Sort[MLSortBy.type]] = None,
      filter: Option[Filter[MLFilterBy.type]] = None) = {
    
    val queryParams = ListBuffer[QueryParam[MLMapper]](
      StartAt(range.offset), MaxRows(range.limit))
   
    sort match {
      case Some(s) => queryParams += toOrderBy(s)
      case None =>
    }
    
    filter match {
      case Some(f) => queryParams += toBy(f)
      case None =>
    }
    
    MLMetaMapper.findAll(queryParams: _*) map toDomain 
  }
  
  def count(filter: Option[Filter[MLFilterBy.type]] = None) =
    filter match {
      case None => MLMetaMapper.count
      case Some(f) => MLMetaMapper.count(toBy(f))
    }
  
  /**
   * ソート方法を OrderBy オブジェクトに変換する
   * 
   * @param sort ソート方法
   * @return OrderBy オブジェクト
   */
  def toOrderBy(sort: Sort[MLSortBy.type]) = {
    import org.milmsearch.core.domain.{MLSortBy => S}
    import mapper.{MLMetaMapper => M}
    
    val field = sort.column match {
      case S.Title => M.title
      case S.LastMailedAt => M.lastMailedAt
      case _ => throw new NoSuchFieldException(
        "Can't convert Filter to By")
    }
    
    OrderBy(field, toAscOrDesc(sort.sortOrder))
  }
  
  /**
   * 検索条件を By オブジェクトに変換する
   * 
   * @param filter 検索条件
   * @return By オブジェクト 
   */
  private def toBy(filter: Filter[MLFilterBy.type]) = filter match {
    case Filter(MLFilterBy.Title, v: String) =>
      By(MLMetaMapper.title, v)
    case _ => throw new NoSuchFieldException(
      "Can't convert Filter to By")
  }
  
  /**
   * Mapper オブジェクトをML情報に変換する
   * 
   * @param mapper Mapper オブジェクト
   * @return ML情報
   */
  private def toDomain(mapper: MLMapper) = ML(
    id           = mapper.id,
    title        = mapper.title,
    archiveType  = MLArchiveType.withName(mapper.archiveType),
    archiveURL   = new URL(mapper.archiveURL),
    lastMailedAt = new DateTime(mapper.lastMailedAt.getTime),
    approvedAt   = new DateTime(mapper.approvedAt.getTime))

  /**
   * ML情報作成要求を Mapper オブジェクトに変換する
   * 
   * @param req ML情報作成要求
   * @return Mapper オブジェクト
   */
  private def toMapper(req: CreateMLRequest): MLMapper =
    MLMetaMapper.create
      .title(req.title)
      .archiveType(req.archiveType.toString)
      .archiveURL(req.archiveURL.toString)
      .lastMailedAt(null)
      .approvedAt(req.approvedAt.toDate)
}

/**
 * O/R マッパー
 */
package mapper {

  /**
   * ML情報テーブルの操作を行う
   */
  private[dao] object MLMetaMapper extends MLMapper
      with LongKeyedMetaMapper[MLMapper] {
    override def dbTableName = "ml"
    override def fieldOrder = List(id, title,
      archiveType, archiveURL, lastMailedAt, approvedAt)
  }

  /**
   * ML情報のモデルクラス
   */
  private[dao] class MLMapper extends
      LongKeyedMapper[MLMapper] with IdPK {
    def getSingleton = MLMetaMapper
    object title extends MappedString(this, 200)
    object archiveType extends MappedString(this, 200)
    object archiveURL extends MappedText(this)
    object lastMailedAt extends MappedDateTime(this)
    object approvedAt extends MappedDateTime(this)
  }
}
