package org.milmsearch.core.service
import org.joda.time.DateTime
import org.joda.time.DateTimeZone

/**
 * 日付・時刻関連サービス
 */
trait DateTimeService {
  /**
   * 現在時刻を取得する
   * 
   * @return 現在時刻
   */
  def now(): DateTime
}

class DateTimeServiceImpl extends DateTimeService {
  def now() = new DateTime(DateTimeZone.UTC)
}