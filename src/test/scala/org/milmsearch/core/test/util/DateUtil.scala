package org.milmsearch.core.test.util
import org.apache.commons.lang3.time.DateUtils
import org.joda.time.DateTime

object DateUtil {

    def createDate(dateStr: String) = 
      DateUtils.parseDate(dateStr, {"yyyy/MM/dd HH:mm:ss"})
    
    /**
     * Date オブジェクトを生成する
     * 
     * @param year 年
     * @param monthOfYear 月 (January -> 1)
     * @param dayOfMonth 日
     */
    def newDate(year: Int, monthOfYear: Int, dayOfMonth: Int) =
      newDateTime(year, monthOfYear, dayOfMonth).toDate
    
    /**
     * DateTime オブジェクトを生成する
     * 
     * @param year 年
     * @param monthOfYear 月 (January -> 1)
     * @param dayOfMonth 日
     */
    def newDateTime(year: Int, monthOfYear: Int, dayOfMonth: Int) =
      new DateTime(year, monthOfYear, dayOfMonth, 0, 0, 0, 0)

}