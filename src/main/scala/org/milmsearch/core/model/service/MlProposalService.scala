package org.milmsearch.core.model.service
import java.net.URL
import net.liftweb.mapper.QueryParam
import org.milmsearch.core.model.dao.MlProposalDaoComponent

/**
 * ML登録申請情報のコンパニオンオブジェクト
 */
/*
object MlProposal {

  /**
   * Mapperからインスタンス生成する
   * 
   * @param mapper Mapperオブジェクト
   * @return (ML登録申請情報, ID)
   */
  def fromMapper(mapper: MlProposalMapper): (MlProposal, Long) = {
    // TODO
    (
      MlProposal(
        "サンプル 太郎",
        "sample@example.com",
        "サンプルML",
        MlProposalStatus.New
      ),
      0L
    )
  }
}
*/

/**
 * ML登録申請情報
 * 
 * @param proposerName 申請者名
 * @param proposerEmail 申請者メールアドレス
 * @param mlTitle MLのタイトル
 * @param status 申請状況
 * @param archiveType MLのアーカイブのタイプ
 * @param archiveUrl MLのアーカイブページのURL
 * @param comment コメント
 */
case class MlProposal(
  proposerName: String,
  proposerEmail: String,
  mlTitle: String,
  status: MlProposalStatus.Value,
  archiveType: Option[MlArchiveType.Value] = None,
  archiveUrl: Option[URL] = None,
  comment: Option[String] = None
)

/**
 * MLのアーカイブのタイプ(ソフトウェア)
 */
object MlArchiveType extends Enumeration {
  val Mailman = Value
}

/**
 * ML登録申請の状態
 */
object MlProposalStatus extends Enumeration {
  val New, Accepted, Rejected = Value
}

/**
 * ML登録申請情報を管理するサービス
 */
trait MlProposalServiceComponent {
  
  // 依存クラス(RegistryがDIする)
  this: MlProposalDaoComponent =>

  // 自身のインスタンス(Registryで実体化する)
  val mlProposalService: MlProposalService

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
}