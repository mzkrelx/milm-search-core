package org.milmsearch.core.service

import org.milmsearch.core.domain.Filter
import org.milmsearch.core.domain.MlProposal
import org.milmsearch.core.domain.MlProposalSearchResult
import org.milmsearch.core.domain.MlProposalSearchResult
import org.milmsearch.core.domain.Page
import org.milmsearch.core.domain.Sort
import org.milmsearch.core.ComponentRegistry
import org.milmsearch.core.domain.CreateMlProposalRequest
import org.milmsearch.core.domain.MlProposal

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
   * @param page   取得するページ番号と1ページあたりの件数
   * @param sort   ソート方法
   * @return 検索結果情報 
   */
  def search(page: Page, sort: Sort): MlProposalSearchResult 
  
  /**
   * 検索結果情報を取得する
   * 
   * @param filter 絞り込み条件
   * @param page   取得するページ番号と1ページあたりの件数
   * @param sort   ソート方法
   * @return 検索結果情報 
   */
  def search(filter: Filter, page: Page, sort: Sort): MlProposalSearchResult 
  
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
 * MlProposalService の実装クラス
 */
class MlProposalServiceImpl extends MlProposalService {

  /**
   * ML登録申請情報 DAO
   */
  private def mpDao = ComponentRegistry.mlProposalDao()

  def create(request: CreateMlProposalRequest) = mpDao.create(request)

  def search(page: Page, sort: Sort): MlProposalSearchResult = {
    val mlProposals = mpDao.findAll(page.toRange, sort)
    val itemsPerPage = if (mlProposals.lengthCompare(page.count.toInt) < 0) 
      page.count else mlProposals.length
    MlProposalSearchResult(mpDao.count(), page.toRange.offset, itemsPerPage, mlProposals)
  }

  def search(filter: Filter, page: Page, sort: Sort): MlProposalSearchResult = {
    val mlProposals = mpDao.findAll(filter, page.toRange, sort)
    val itemsPerPage = if (mlProposals.lengthCompare(page.count.toInt) < 0) 
      page.count else mlProposals.length
    MlProposalSearchResult(mpDao.count(filter), page.toRange.offset, itemsPerPage, mlProposals)
  }

  def findById(id: Long) = None // TODO

  def update(id: Long, proposal: MlProposal) = false // TODO

  def delete(id: Long) = false // TODO
}
