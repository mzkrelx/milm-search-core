package org.milmsearch.core.service

import org.milmsearch.core.domain.CreateMlProposalRequest
import org.milmsearch.core.domain.Filter
import org.milmsearch.core.domain.MlProposal
import org.milmsearch.core.domain.{MlProposalFilterBy => MLPFilterBy}
import org.milmsearch.core.domain.{MlProposalSearchResult => MLPSearchResult}
import org.milmsearch.core.domain.{MlProposalSortBy => MLPSortBy}
import org.milmsearch.core.domain.Page
import org.milmsearch.core.domain.Sort
import org.milmsearch.core.ComponentRegistry

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
  def search(filter: Option[Filter[MLPFilterBy.type]],
      page: Page,
      sort: Option[Sort[MLPSortBy.type]]): MLPSearchResult

  /**
   * ML登録申請情報を取得する
   *
   * @param id ID
   * @return ML登録申請情報
   */
  def findById(id: Long): Option[MlProposal]

  /**
   * ML登録申請情報を更新する
   *
   * @param id ID
   * @param mlProposal ML登録申請情報
   * @return 更新対象が存在したかどうか
   */
  def update(id: Long, mlProposal: MlProposal): Boolean


  /**
   * ML登録申請情報を削除する
   *
   * @param id ID
   * @return 削除対象が存在したかどうか
   */
  def delete(id: Long): Boolean
}

/**
 * 検索に失敗したときの例外
 */
class SearchFailedException(msg: String) extends Exception(msg)

/**
 * MlProposalService の実装クラス
 */
class MlProposalServiceImpl extends MlProposalService {

  /**
   * ML登録申請情報 DAO
   */
  private def mpDao = ComponentRegistry.mlProposalDao()

  def create(request: CreateMlProposalRequest) = mpDao.create(request)

  def search(filter: Option[Filter[MLPFilterBy.type]],
      page: Page,
      sort: Option[Sort[MLPSortBy.type]]): MLPSearchResult = {
    val mlProposals = mpDao.findAll(filter, page.toRange, sort)
    MLPSearchResult(
      mpDao.count(filter),
      page.toRange.offset + 1,
      mlProposals.length.toLong min page.count, mlProposals)
  }

  def findById(id: Long) = None // TODO

  def update(id: Long, proposal: MlProposal) = false // TODO

  def delete(id: Long) = false // TODO
}
