package org.milmsearch.core.api
import java.net.URI
import java.net.URL

import org.apache.commons.lang3.time.DateFormatUtils
import org.milmsearch.core.domain.Filter
import org.milmsearch.core.domain.MlArchiveType
import org.milmsearch.core.domain.CreateMlProposalRequest
import org.milmsearch.core.domain.MlProposalSearchResult
import org.milmsearch.core.domain.MlProposalStatus
import org.milmsearch.core.domain.Page
import org.milmsearch.core.domain.Sort
import org.milmsearch.core.domain.SortOrder
import org.milmsearch.core.ComponentRegistry

import javax.ws.rs.core.Response
import javax.ws.rs.Consumes
import javax.ws.rs.DELETE
import javax.ws.rs.DefaultValue
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.PUT
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam
import net.liftweb.common.Logger
import net.liftweb.json.DefaultFormats
import net.liftweb.json.FieldSerializer
import net.liftweb.json.Serialization
import net.liftweb.json.ShortTypeHints
import net.liftweb.json.parse

class BadQueryParameterException(msg: String) extends Exception(msg)
  
/**
 * ML登録申請情報のAPIリソース
 */
@Path("/ml-proposals")
class MlProposalResource extends Logger {
  // for lift-json
  implicit val formats = DefaultFormats

  /** ML登録申請管理サービス */
  private def mpService = ComponentRegistry.mlProposalService.vend
  
  /**
   * GETのクエリパラムのデフォルト値
   */
  private val defaultSortBy = "id"
  private val defaultStartPage = 1
  private val defaultCount = 10
  
  /** 1ページの項目数の上限値 */
  private val maxCount = 100

  private val dateFormat = DateFormatUtils.ISO_DATETIME_FORMAT

  /**
   * ML登録申請情報を作成する<br/>
   * 下記はリクエストボディの例
   * <pre>
   * {
   *   "proposerName": "申請者の名前",
   *   "proposerEmail": "申請者のメールアドレス",
   *   "mlTitle": "MLタイトル(ML名)",
   *   "status": "new",
   *   "archiveType": "メールアーカイブの種類(ex. mailman)",
   *   "archiveUrl": "メールアーカイブの基底URL",
   *   "comment": "コメント(MLの説明など)"
   * }
   * </pre>
   * 
   * @param requestBody JSON形式のML登録申請情報
   * @return 201(Created)
   */
  @POST
  @Consumes(Array("application/json"))
  def create(requestBody: String) = {
    val dto = parse(requestBody).extract[RequestDto]
    val id = mpService.create(dto.toDomain)

    Response.created(
      new URI("/ml-proposal/" + id)
    ).build()
  }

  /**
   * "?filterBy=絞り込み項目&
filterValue=絞り込み項目の値&
sortBy=ソート列名&
sortOrder=昇順か逆順か&
count=1ページの項目数&
startPage=ぺージ番号"
"Accept: ""application/json""
Accept-Charset: utf-8"
   * 
   * @param filterBy 絞り込み項目
   * @param filterValue 絞り込み項目の値
   * @param sortBy ソート列名
   * @param sortOrder 昇順か逆順か
   * @param count 1ページの項目数
   * @param startPage ぺージ番号
   * @return 200(OK)
   */
  @GET
  @Produces(Array("application/json"))
  def list(@QueryParam("filterBy") filterBy: String,
           @QueryParam("filterValue") filterValue: String,
           @QueryParam("sortBy") sortBy: String,
           @QueryParam("sortOrder") sortOrder: String,
           @QueryParam("startPage") startPage: Int,
           @QueryParam("count") count: Int) = {
    try {
      getList(filterBy, filterValue, sortBy, sortOrder, startPage, count)
    } catch {
      case e: BadQueryParameterException => {
        error(e)
        Response.status(Response.Status.BAD_REQUEST).build()
      }
    }
  }

  @Path("{id}")
  @GET
  def show() = {
    Response.serverError().build()
  }

  @Path("{id}")
  @PUT
  def update() = {
    Response.serverError().build()
  }

  @Path("{id}")
  @DELETE
  def delete() = {
    Response.serverError().build()
  }

  /**
   * リクエストボディの変換用オブジェクト
   */
  case class RequestDto(
    proposerName: String,
    proposerEmail: String,
    mlTitle: String,
    status: String,
    archiveType: String,
    archiveUrl: String,
    comment: String
  ) {

    /**
     * ドメインオブジェクトに変換する
     */
    def toDomain =
      CreateMlProposalRequest(
        proposerName,
        proposerEmail,
        mlTitle,
        MlProposalStatus.withName(status),
        Some(MlArchiveType.withName(archiveType)),
        Some(new URL(archiveUrl)),
        Some(comment)
      )
  }

  private def getList(filterBy: String, filterValue: String, sortBy: String, 
      sortOrder: String, startPage: Int, count: Int): javax.ws.rs.core.Response = {
    val filter = (Option(filterBy), Option(filterValue)) match {
      case (None, Some(value)) => throw new BadQueryParameterException(
          "Invalid filter. Please query filterBy and filterValue at the same time.")
      case (Some(by), None) => throw new BadQueryParameterException(
          "Invalid filter. Please query filterBy and filterValue at the same time.")
      case (Some(by), Some(value)) => Some(Filter(Symbol(by), value))
      case _ => None
    }
    
    if (startPage < 0) throw new BadQueryParameterException(
        "Invalid startPage value. [%d]" format startPage)
    if (count < 0 | count > maxCount) throw new BadQueryParameterException(
        "Invalid count value. [%d]" format count)

    val page = Page(
      if (startPage == 0) defaultStartPage else startPage,
      if (count == 0) defaultCount else count
    )

    val sort = Sort(
      if (sortBy == null) Symbol(defaultSortBy) else Symbol(sortBy),
      sortOrder match {
        case "ascending" | null => SortOrder.Ascending
        case "descending" => SortOrder.Descending
        case other => throw new BadQueryParameterException(
            "Invalid sortOrder value. [%s]" format other)
      }
    )
    val searchResult = filter match {
      case Some(_) => mpService.search(filter.get, page, sort)
      case None => mpService.search(page, sort)
    }
 
    val searchResultDto = SearchResultDto(
      searchResult.totalResults,
      searchResult.startIndex,
      searchResult.itemsPerPage,
      searchResult.mlProposals map { m =>
        MlProposalDto(
          m.id, m.proposerName, m.proposerEmail, m.mlTitle, m.status.toString,
          m.archiveType map { _.toString } getOrElse "",
          m.archiveUrl map { _.toString } getOrElse "",
          m.comment getOrElse "", 
          dateFormat.format(m.createdAt), 
          dateFormat.format(m.updatedAt)
        )
      }
    )

    val json = Serialization.write(searchResultDto)
    Response.ok(json).build()
  }
}

  
/**
 * ML登録申請ドメインの変換用オブジェクト
 */
case class MlProposalDto(
  id: Long,
  proposerName: String,
  proposerEmail: String,
  mlTitle: String,
  status: String,
  archiveType: String,
  archiveUrl: String,
  comment: String,
  createdAt: String,
  updatedAt: String
)

/**
 * ML登録申請検索結果の変換用オブジェクト
 */
case class SearchResultDto(
  totalResults: Long,
  startIndex: Long,
  itemsPerPage: Long,
  mlProposals: List[MlProposalDto]
)
  
  
