package org.milmsearch.core.service

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
   * 検索条件に合致するML登録申請情報を検索する
   * 
   * @param range 検索結果の取得範囲
   * @param sort 検索結果のソート方法
   * @return (ML登録申請情報, ID)のリスト 
   */
  def find(range: Range, sort: Sort*): List[MlProposal]

  /**
   * ML登録申請情報を検索する
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

  def find(range: Range, sort: Sort*) = Nil // TODO

  def findById(id: Long) = None // TODO

  def update(id: Long, proposal: MlProposal) = false // TODO

  def delete(id: Long) = false // TODO
}