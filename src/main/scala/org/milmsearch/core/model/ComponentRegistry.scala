package org.milmsearch.core.model
import org.milmsearch.core.model.service.MlProposalServiceComponent
import org.milmsearch.core.model.dao.MlProposalDaoComponent

/**
 * CakePattern を用いた DIコンテナ<br/>
 * Service と DAO の初期化を行う
 */
object ComponentRegistry extends
  MlProposalServiceComponent with
  MlProposalDaoComponent
{
  val mlProposalService = new MlProposalService
  private[model] val mlProposalDao = new MlProposalDao
}