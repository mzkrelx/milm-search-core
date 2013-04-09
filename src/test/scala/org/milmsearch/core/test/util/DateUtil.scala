/*
 * MilmSearch is a mailing list searching system.
 *
 * Copyright (C) 2013 MilmSearch Project.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 *
 * You can contact MilmSearch Project at mailing list
 * milm-search-public@lists.sourceforge.jp.
 */
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