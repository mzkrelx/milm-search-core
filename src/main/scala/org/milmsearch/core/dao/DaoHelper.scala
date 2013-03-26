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
package org.milmsearch.core.dao
import net.liftweb.mapper.Schemifier
import mapper._
import net.liftweb.mapper.AscOrDesc
import net.liftweb.mapper.Descending
import net.liftweb.mapper.Ascending
import org.milmsearch.core.domain.SortOrder

/**
 * DAO 関連のヘルパークラス
 */
object DaoHelper {
  /**
   * O/R マッパーとして定義済みの テーブル や カラムが
   * 存在しない場合、DBに対してそれらを作成する
   */
  def schemify() {
    Schemifier.schemify(true, Schemifier.infoF _,
      MLProposalMetaMapper,
      MLMetaMapper
    )
  }

  /**
   * ドメインのソートをマッパーのソートに変換します。
   */
  def toAscOrDesc(order: SortOrder.Value): AscOrDesc =
    order match {
      case SortOrder.Ascending => Ascending
      case SortOrder.Descending => Descending
    }
}