package org.milmsearch.core
import dao._
import service._
import net.liftweb.util.SimpleInjector

/**
 * サービスロケーター<br/>
 * Service と DAO の初期化を行う
 */
object ComponentRegistry extends SimpleInjector {
  val mlProposalDao = new Inject[MlProposalDao](new MlProposalDaoImpl) {}
  val mlProposalService = new Inject[MlProposalService](new MlProposalServiceImpl) {}
  val dateTimeService = new Inject[DateTimeService](new DateTimeServiceImpl) {}
}