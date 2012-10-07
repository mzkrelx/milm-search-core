package org.milmsearch.core

import org.milmsearch.common.LoggingUtil.withErrlog
import org.milmsearch.common.LoggingUtil.withErrlogQuietly

import javax.servlet.ServletContextEvent
import javax.servlet.ServletContextListener

import net.liftweb.common.Box.box2Option
import net.liftweb.common.Loggable
import net.liftweb.db.DB1.db1ToDb
import net.liftweb.db.ProtoDBVendor
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
   * DB接続マネージャ
   */
  private var dbManager: Option[ProtoDBVendor] = None

  override def contextInitialized(event: ServletContextEvent) {
    withErrlog { initializeDBCon() }
  }

  /**
   * データベース接続を初期化する
   */
  private def initializeDBCon() {
    val vendor = new StandardDBVendor(
      Props.get("db.driver").get, // TODO default value
      Props.get("db.url").get,    // TODO default value
      Props.get("db.user"),
      Props.get("db.password")
    )

    DB.defineConnectionManager(
      DefaultConnectionIdentifier, vendor)

    dbManager = Some(vendor)
  }

  override def contextDestroyed(finalizeevent: ServletContextEvent) {
    withErrlogQuietly { closeDBCon() }
  }

  /**
   * データベース接続を切断する
   */
  private def closeDBCon() {
    dbManager foreach { _.closeAllConnections_!() }
  }

}
