package org.milmsearch.core.test.util
import org.apache.commons.lang3.time.DateUtils

object DateUtil {

    def createDate(dateStr: String) = 
      DateUtils.parseDate(dateStr, {"yyyy/MM/dd HH:mm:ss"})

}