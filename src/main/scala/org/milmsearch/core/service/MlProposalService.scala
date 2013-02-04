package org.milmsearch.core.service

import org.milmsearch.core.domain.CreateMlProposalRequest
import org.milmsearch.core.domain.Filter
import org.milmsearch.core.domain.MlProposal
import org.milmsearch.core.domain.{MlProposalFilterBy => MLPFilterBy}
import org.milmsearch.core.domain.{MlProposalSearchResult => MLPSearchResult}
import org.milmsearch.core.domain.{MlProposalSortBy => MLPSortBy}
import org.milmsearch.core.domain.Page
import org.milmsearch.core.domain.Sort
import org.milmsearch.core.exception.DeleteFailedException
import org.milmsearch.core.exception.ResourceNotFoundException
import org.milmsearch.core.ComponentRegistry
import net.liftweb.common.Loggable
import org.milmsearch.core.domain.MlProposalColumn
import org.milmsearch.core.domain.MlProposalStatus

/**
 * ML登録申請情報を管理するサービス
 */
trait MlProposalService {

  /**
   * ML登録申請情報を作成する
   *
   * @param mlProposal ML登録申請情報
   * @return ID
   */
  def create(request: CreateMlProposalRequest): Long

  /**
   * 検索結果情報を取得する
   *
   * @param filter 検索条件
   * @param page   取得するページ番号と1ページあたりの件数
   * @param sort   ソート方法
   * @return 検索結果情報
   */
  def search(page: Page,
      sort: Option[Sort[MLPSortBy.type]] = None,
      filter: Option[Filter[MLPFilterBy.type]] = None):
      MLPSearchResult

  /**
   * ML登録申請情報を取得する
   *
   * @param id ID
   * @return ML登録申請情報
   */
  def find(id: Long): Option[MlProposal]

  /**
   * ML登録申請情報を更新する
   *
   * @param id ID
   * @param UpdateMlProposalRequest ML登録申請情報
   * @return 更新対象が存在したかどうか
   */
  def update(id: Long, request: CreateMlProposalRequest): Boolean

  /**
   * ML登録申請情報を削除する
   *
   * @param id ID
   */
  @throws(classOf[ResourceNotFoundException])
  @throws(classOf[DeleteFailedException])
  def delete(id: Long)

  /**
   * ML登録申請情報を承認する
   *
   * @param id ID
   */
  def accept(id: Long)
}

/**
 * 検索に失敗したときの例外
 */
class SearchFailedException(msg: String) extends Exception(msg)

/**
 * MlProposalService の実装クラス
 */
class MlProposalServiceImpl extends MlProposalService with Loggable {

  /**
   * ML登録申請情報 DAO
   */
  private def mpDao = ComponentRegistry.mlProposalDao()

  def create(request: CreateMlProposalRequest) =
    mpDao.create(request)

  def search(page: Page,
      sort: Option[Sort[MLPSortBy.type]] = None,
      filter: Option[Filter[MLPFilterBy.type]] = None) = {
    val mlProposals = mpDao.findAll(page.toRange, sort, filter)
    MLPSearchResult(
      mpDao.count(filter),
      page.getStartIndex,
      mlProposals.length.toLong min page.count, mlProposals)
  }

  def find(id: Long) = mpDao.find(id)

  def update(id: Long, request: CreateMlProposalRequest) = mpDao.update(id, request)

  def delete(id: Long) {
    try {
      val isDeleted = mpDao.delete(id)
      if (! isDeleted) {
        throw new ResourceNotFoundException("Not found.")
      }
    } catch {
      case e: ResourceNotFoundException => {
      	logger.error(e)
      	throw e
      }
      case e => {
        logger.error(e)
        throw new DeleteFailedException(
          "Delete failed.")
      }
    }
  }

  def accept(id: Long) {
    if (!mpDao.update(id, MlProposalColumn.Status,
        MlProposalStatus.Accepted.toString)) {
      throw new ResourceNotFoundException(
        "MlProposal to accept is not found.")
    }
  }
}
