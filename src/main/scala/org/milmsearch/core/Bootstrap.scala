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
import net.liftweb.util.Props

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
    withErrlog { initializeDBCon() }
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

    lazy val defaultUrl = Props.mode match {
      case Props.RunModes.Production => "jdbc:postgresql:milmsearch"
      case _ => {
        startH2Server()
        "jdbc:h2:mem:milmsearch;DB_CLOSE_DELAY=-1"
      }
    }

    val vendor = new StandardDBVendor(
      Props.get("db.driver") openOr defaultDriver,
      Props.get("db.url") openOr defaultUrl,
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
    DaoHelper.schemify()
  }

  override def contextDestroyed(finalizeevent: ServletContextEvent) {
    finalizeHooks foreach { f =>
      withErrlogQuietly { f() }
    }
  }
}
