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
import net.liftweb.common.LazyLoggable
import net.liftweb.common.Box

/**
 * ログ関連ユーティリティ
 */
object LoggingUtil extends LazyLoggable {

  /**
   * 例外発生時にエラーログを出力する<br/>
   * catch した例外は throw する
   *
   * @param f 例外を投げる可能性のある処理
   * @return 処理 f の戻り値
   */
  def withErrlog[T](f: => T): T =
    try {
      f
    } catch {
      case e => {
        logger.error(e.getMessage(), e)
        throw e
      }
    }

  /**
   * 例外発生時にエラーログを出力する<br/>
   * catch した例外は黙殺する
   *
   * @param f 例外を投げる可能性のある処理
   */
  def withErrlogQuietly(f: => Unit) {
    try {
      f
    } catch {
      case e => logger.error(e.getMessage(), e)
    }
  }
}
