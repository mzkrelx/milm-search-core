package org.milmsearch.core.dao
import net.liftweb.mapper.Schemifier
import mapper._

/**
 * DAO 関連のヘルパークラス
 */
object DaoHelper {
  /**
   * O/R マッパーとして定義済みの テーブル や カラムが
   * 存在しない場合、DBに対してそれらを作成する
   */
  def schemify() {
    Schemifier.schemify(true, Schemifier.infoF _,
      MlProposalMetaMapper
    )
  }
}