package org.milmsearch.core.dao

import net.liftweb.common.Loggable
import net.liftweb.mapper.IdPK
import net.liftweb.mapper.LongKeyedMapper
import net.liftweb.mapper.LongKeyedMetaMapper
import net.liftweb.mapper.MappedString
import net.liftweb.mapper.MappedText
import net.liftweb.mapper.MappedDateTime
import org.milmsearch.core.domain.CreateMLRequest

/**
 * ML情報 の DAO
 */
trait MLDao {
  /**
   * ML情報を永続化する
   */
  def create(req: CreateMLRequest): Long
}

/**
 * MLDao の実装クラス
 */
class MLDaoImpl extends MLDao with Loggable {
  def create(req: CreateMLRequest) = toMapper(req).saveMe().id

  /**
   * ML情報ドメインを Mapper オブジェクトに変換する
   */
  private def toMapper(req: CreateMLRequest): mapper.MLMapper =
    mapper.MLMetaMapper.create
      .title(req.title)
      .archiveType(req.archiveType.toString)
      .archiveURL(req.archiveURL.toString)
      .lastMailedAt(null)
      .approvedAt(req.approvedAt)
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
