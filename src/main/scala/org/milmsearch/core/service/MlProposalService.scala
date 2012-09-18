package org.milmsearch.core.service
import java.net.URL
import net.liftweb.mapper.QueryParam
import org.milmsearch.core.domain.MlProposal

/**
 * ML登録申請情報を管理するサービス
 */
  /**
   * Service の実体
   */
class MlProposalService {
  
  /**
   * ML登録申請情報を作成する
   * 
   * @param proposal ML登録申請情報
   * @return ID
   */
  def create(proposal: MlProposal): Long = {
    // TODO
    0L
  }

  /**
   * 検索条件に合致するML登録申請情報を検索する
   * 
   * @param queryParams 検索条件
   * @return (ML登録申請情報, ID)のリスト 
   */
  // TODO
//    def findAll(?):
//        List[(MlProposal, Long)] = {
//      // TODO
//      Nil
//    }

  /**
   * ML登録申請情報を検索する
   * 
   * @param id ID
   * @return ML登録申請情報のリスト
   */
  def find(id: Long): Option[MlProposal] = {
    // TODO
    None
  }

  /**
   * ML登録申請情報を更新する
   * 
   * @param id ID
   * @param proposal ML登録申請情報
   * @return 更新対象が存在したかどうか
   */
  def update(id: Long, proposal: MlProposal): Boolean = {
    // TODO
    false
  }

  /**
   * ML登録申請情報を削除する
   * 
   * @param id ID
   * @return 削除対象が存在したかどうか
   */
  def delete(id: Long): Boolean = {
    // TODO
    false
  }

}