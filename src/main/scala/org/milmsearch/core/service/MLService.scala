package org.milmsearch.core.service
import org.milmsearch.core.ComponentRegistry
import org.milmsearch.core.domain.CreateMlProposalRequest
import org.milmsearch.core.exception.ResourceNotFoundException
import org.milmsearch.core.domain.MlProposal
import org.milmsearch.core.exception.DeleteFailedException
import org.milmsearch.core.domain.ML
import org.milmsearch.core.domain.CreateMLRequest
import org.milmsearch.core.domain.MLSearchResult
import org.milmsearch.core.domain.Page
import net.liftweb.common.Loggable
import org.milmsearch.core.domain.Filter
import org.milmsearch.core.domain.Sort
import org.milmsearch.core.domain.MLSortBy
import org.milmsearch.core.domain.MLFilterBy

/**
 * ML情報を管理するサービス
 */
trait MLService {

  /**
   * ML情報を作成する
   *
   * @param req ML情報の作成要求
   * @return ID
   */
  def create(req: CreateMLRequest): Long

  /**
   * MLの検索結果情報を取得する
   *
   * @param filter 検索条件
   * @param page   取得するページ番号と1ページあたりの件数
   * @param sort   ソート方法
   * @return 検索結果情報
   */
  def search(page: Page,
      sort: Option[Sort[MLSortBy.type]] = None,
      filter: Option[Filter[MLFilterBy.type]] = None): MLSearchResult

  /**
   * ML情報を取得する
   *
   * @param id ID
   * @return ML情報
   */
  def find(id: Long): ML
}

/**
 * MLService の実装クラス
 */
class MLServiceImpl extends MLService with Loggable {

  /** ML情報 DAO */
  private def mlDao = ComponentRegistry.mlDao()

  def create(req: CreateMLRequest) = mlDao.create(req)

  def search(page: Page,
      sort: Option[Sort[MLSortBy.type]] = None,
      filter: Option[Filter[MLFilterBy.type]] = None) = null // TODO

  def find(id: Long) = null // TODO
}
