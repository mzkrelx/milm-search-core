package org.milmsearch.core
import javax.servlet.ServletContextListener
import javax.servlet.ServletContextEvent
import net.liftweb.db.StandardDBVendor
import net.liftweb.http.LiftRules
import net.liftweb.mapper.Schemifier
import net.liftweb.mapper.DB
import net.liftweb.db.DefaultConnectionIdentifier
import org.milmsearch.core.model.dao.DaoHelper
import net.liftweb.common.Box
import org.milmsearch.core.model.ComponentRegistry
import org.milmsearch.core.model.service.MlProposalServiceComponent

/**
 * サーブレットコンテナ(Tomcat等)の起動時に行う処理<br/>
 * 終了時に行う処理もここに書く
 */
class Bootstrap extends ServletContextListener {

  override def contextInitialized(event: ServletContextEvent) {
  }

  override def contextDestroyed(finalizeevent: ServletContextEvent) {
  }

}
