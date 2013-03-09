package org.milmsearch.core.api
import net.liftweb.json.Serialization
import net.liftweb.json.DefaultFormats

/**
 * ML登録申請検索結果の変換用オブジェクト
 *
 * D の型は内包するアイテムのDTOの型を指定します。
 */
case class SearchResultDto[D](
    totalResults: Long,
    startIndex: Long,
    itemsPerPage: Long,
    items: List[D]) {
  // for lift-json
  implicit val formats = DefaultFormats

  def toJson(): String = Serialization.write(this)
}
