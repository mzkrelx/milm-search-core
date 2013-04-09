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
package org.milmsearch.core.api
import net.liftweb.json.Serialization
import net.liftweb.json.DefaultFormats

/**
 * ML登録申請検索結果の変換用オブジェクト
 *
 * D の型は内包するアイテムのDTOの型を指定します。
 */
case class SearchResultDto[D](
    totalResults: Long,
    startIndex: Long,
    itemsPerPage: Long,
    items: List[D]) {
  // for lift-json
  implicit val formats = DefaultFormats

  def toJson(): String = Serialization.write(this)
}
