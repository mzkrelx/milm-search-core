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
import org.milmsearch.core.domain.MlArchiveType
import mapper._
import net.liftweb.common.Full
import net.liftweb.common.Empty
import net.liftweb.common.Failure
import java.net.URL
import org.joda.time.DateTime

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
  
  /**
   * Mapper オブジェクトをML情報ドメインに変換する
   * 
   * @param mapper Mapper オブジェクト
   */
  private def toDomain(mapper: MLMapper) = ML(
    id           = mapper.id,
    title        = mapper.title,
    archiveType  = MlArchiveType.withName(mapper.archiveType),
    archiveURL   = new URL(mapper.archiveURL),
    lastMailedAt = new DateTime(mapper.lastMailedAt.getTime),
    approvedAt   = new DateTime(mapper.approvedAt.getTime))

  /**
   * ML情報作成要求を Mapper オブジェクトに変換する
   * 
   * @param req ML情報作成要求
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
