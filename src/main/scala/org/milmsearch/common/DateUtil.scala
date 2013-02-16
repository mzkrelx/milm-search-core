package org.milmsearch.common
import org.joda.time.format.ISODateTimeFormat
import org.joda.time.DateTime

/**
 * 日付関連ユーティリティ
 */
object DateUtil {

  /**
   * DateTime オブジェクトを ISO8601 形式の文字列に変換する
   * 
   * @dateTime 変換したい日時
   * @return 変換済み文字列 (ex. 2004-06-09T10:20:30+09:00)
   */
  def formatToISO(dateTime: DateTime) =
    ISODateTimeFormat.dateTimeNoMillis().print(dateTime)
}
