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
package org.milmsearch.core.test

import scala.collection.mutable.ListBuffer
import net.liftweb.common.Logger
import net.liftweb.db.DefaultConnectionIdentifier
import net.liftweb.db.StandardDBVendor
import net.liftweb.mapper.DB
import net.liftweb.util.Props
import net.liftweb.mapper.MapperRules
import net.liftweb.util.StringHelpers

/**
 * テスト開始前(終了後)に実行する処理を書く
 */
object Boot extends Logger {

  /**
   * テスト終了後に実行したい処理のリスト
   */
  private val cleanupHooks: ListBuffer[() => Unit] = ListBuffer()

  /**
   * テスト開始前に実行する処理
   */
  def setup() {
    try { initializeDBCon() } catch { case e => error(e); throw e }
  }

  /**
   * データベース接続を初期化する
   */
  private def initializeDBCon() {
    lazy val defaultDriver = "org.h2.Driver"
    lazy val defaultURL = "jdbc:h2:mem:milmsearch_test;DB_CLOSE_DELAY=-1"

    val vendor = new StandardDBVendor(
      Props.get("db.driver") openOr defaultDriver,
      Props.get("db.url") openOr defaultURL,
      Props.get("db.user"),
      Props.get("db.password")
    )

    DB.defineConnectionManager(DefaultConnectionIdentifier, vendor)

    cleanupHooks += vendor.closeAllConnections_!

    // テーブル名・カラム名には snake_case を用いる
    MapperRules.tableName  = (_, name) => StringHelpers.snakify(name)
    MapperRules.columnName = (_, name) => StringHelpers.snakify(name)
  }

  /**
   * テスト終了後に実行する処理
   */
  def cleanup() {
    cleanupHooks foreach { f =>
      try { f() } catch { case e => error(e) }
    }
  }

}
