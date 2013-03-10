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
package org.milmsearch.core

import scala.collection.mutable.ListBuffer

import org.h2.tools.Server
import org.milmsearch.common.LoggingUtil.withErrlog
import org.milmsearch.common.LoggingUtil.withErrlogQuietly
import org.milmsearch.core.dao.DaoHelper

import javax.servlet.ServletContextEvent
import javax.servlet.ServletContextListener

import net.liftweb.common.Loggable
import net.liftweb.db.DB1.db1ToDb
import net.liftweb.mapper.DefaultConnectionIdentifier
import net.liftweb.mapper.StandardDBVendor
import net.liftweb.mapper.DB
import net.liftweb.mapper.MapperRules
import net.liftweb.util.Props
import net.liftweb.util.StringHelpers

/**
 * サーブレットコンテナ(Tomcat等)の起動時に行う処理<br/>
 * 終了時に行う処理もここに書く
 */
class Bootstrap extends ServletContextListener with Loggable {

  /**
   * コンテキスト終了時に実行したい処理のリスト
   */
  private val finalizeHooks: ListBuffer[() => Unit] = ListBuffer()

  override def contextInitialized(event: ServletContextEvent) {
    withErrlog {
      initializeDBCon()
      schemifyDBTable()
    }
  }

  /**
   * データベース接続を初期化する
   */
  private def initializeDBCon() {
    def startH2Server() {
      val webServer = Server.createWebServer().start()
      val tcpServer = Server.createTcpServer().start()

      logger.info("H2 Web Console Started. (%s)" format webServer.getURL())
      logger.info("H2 Tcp Server Started. (jdbc:h2:%s/mem:milmsearch)" format tcpServer.getURL())

      finalizeHooks += webServer.stop
      finalizeHooks += tcpServer.stop
    }

    lazy val defaultDriver = Props.mode match {
      case Props.RunModes.Production => "org.postgresql.Driver"
      case _ => "org.h2.Driver"
    }

    lazy val defaultURL = Props.mode match {
      case Props.RunModes.Production => "jdbc:postgresql:milmsearch"
      case _ => {
        startH2Server()
        "jdbc:h2:mem:milmsearch;DB_CLOSE_DELAY=-1"
      }
    }

    val vendor = new StandardDBVendor(
      Props.get("db.driver") openOr defaultDriver,
      Props.get("db.url") openOr defaultURL,
      Props.get("db.user"),
      Props.get("db.password"))

    DB.defineConnectionManager(
      DefaultConnectionIdentifier, vendor)

    finalizeHooks += vendor.closeAllConnections_!
  }

  /**
   * データベーステーブルのスキーマの最適化を行う<br/>
   * (状況に応じて CREATE TABLE や ALTER TABLE が走る)
   */
  private def schemifyDBTable() {
    // テーブル名・カラム名には snake_case を用いる
    MapperRules.tableName  = (_, name) => StringHelpers.snakify(name)
    MapperRules.columnName = (_, name) => StringHelpers.snakify(name)

    DaoHelper.schemify()
  }

  override def contextDestroyed(finalizeevent: ServletContextEvent) {
    finalizeHooks foreach { f =>
      withErrlogQuietly { f() }
    }
  }

}
