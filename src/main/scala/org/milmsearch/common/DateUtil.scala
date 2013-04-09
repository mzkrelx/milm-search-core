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
