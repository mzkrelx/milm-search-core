package org.milmsearch.core.dao
import net.liftweb.mapper.Schemifier
import mapper._
import net.liftweb.mapper.AscOrDesc
import net.liftweb.mapper.Descending
import net.liftweb.mapper.Ascending
import org.milmsearch.core.domain.SortOrder

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
  
  /**
   * ドメインのソートをマッパーのソートに変換します。
   */
  def toAscOrDesc(order: SortOrder.Value): AscOrDesc = {
    order match {
      case SortOrder.Ascending => Ascending
      case SortOrder.Descending => Descending
    }
  }  
}