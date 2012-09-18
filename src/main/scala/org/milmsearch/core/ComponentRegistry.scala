package org.milmsearch.core
import net.liftweb.util.SimpleInjector
import dao._
import service._

/**
 * Lift Injector を用いた サービスロケーター<br/>
 * Service と DAO の初期化を行う
 */
object ComponentRegistry extends SimpleInjector {
  val mlProposalService = new Inject(new MlProposalService) {}
  val mlProposalDao = new Inject(new MlProposalDao) {}
}