package org.milmsearch.core.api
import java.net.URI
import java.net.URL
import java.util.NoSuchElementException
import org.apache.commons.lang3.time.DateFormatUtils
import org.milmsearch.core.domain.MlArchiveType
import org.milmsearch.core.domain.CreateMlProposalRequest
import org.milmsearch.core.domain.Filter
import org.milmsearch.core.domain.MlArchiveType
import org.milmsearch.core.domain.MlProposal
import org.milmsearch.core.domain.MlProposalFilterBy
import org.milmsearch.core.domain.MlProposalSearchResult
import org.milmsearch.core.domain.MlProposalSortBy
import org.milmsearch.core.domain.MlProposalStatus
import org.milmsearch.core.domain.Page
import org.milmsearch.core.domain.SortOrder
import org.milmsearch.core.ComponentRegistry
import javax.ws.rs.core.Response.Status
import javax.ws.rs.core.Response
import javax.ws.rs.Consumes
import javax.ws.rs.DELETE
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.PUT
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam
import net.liftweb.common.Loggable
import javax.ws.rs.PathParam
//import net.liftweb.json.parse
import net.liftweb.json.DefaultFormats
import net.liftweb.json.Serialization
import net.liftweb.json.parse
import org.milmsearch.core.domain.Sort
import javax.ws.rs.PathParam
import javax.ws.rs.core.Response.Status
import org.milmsearch.core.exception.ResourceNotFoundException

class BadQueryParameterException(msg: String) extends Exception(msg)

class BadRequestException(msg: String) extends Exception(msg)

/**
 * ML登録申請情報のAPIリソース
 */
@Path("/ml-proposals")
class MlProposalResource extends Loggable with PageableResource {
  // for lift-json
  implicit val formats = DefaultFormats

  /** ML登録申請管理サービス */
  private def mpService = ComponentRegistry.mlProposalService.vend

  protected val defaultSortBy = MlProposalSortBy.MlTitle

  private val dateFormat =
    DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT

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
    val dto = parse(requestBody).extract[RequestDto] // リクエストbodyのパース、下記RequestDtoを参照
    val id = mpService.create(dto.toDomain)

    Response.created(
      new URI("/ml-proposal/" + id)).build()
  }

  /**
   * ML登録申請情報の一覧を取得します。
   *
   * @param filterBy 絞り込み項目
   * @param filterValue 絞り込み項目の値
   * @param count 1ページの項目数
   * @param startPage ぺージ番号
   * @param sortBy ソート列名
   * @param sortOrder 昇順か逆順か
   * @return 200(OK) or 400(Bad Request)
   */
  @GET
  @Produces(Array("application/json"))
  def list(@QueryParam("filterBy")    filterBy:    String,
           @QueryParam("filterValue") filterValue: String,
           @QueryParam("startPage")   startPage:   String,
           @QueryParam("count")       count:       String,
           @QueryParam("sortBy")      sortBy:      String,
           @QueryParam("sortOrder")   sortOrder:   String) = {
    try {
      Response.ok(toDto(mpService.search(
        createPage(
          ResourceHelper.getLongParam(startPage, "startPage")
            getOrElse defaultStartPage,
          ResourceHelper.getLongParam(count, "count")
            getOrElse defaultCount),
        createSort(Option(sortBy), Option(sortOrder)),
        createFilter(Option(filterBy), Option(filterValue))
      )).toJson).build()
    } catch {
      case e: BadQueryParameterException => {
        logger.error(e)
        Response.status(Response.Status.BAD_REQUEST).build()
      }
    }
  }

  private def createFilter(filterBy: Option[String],
      filterValue: Option[String]):
      Option[Filter[MlProposalFilterBy.type]] =
    (filterBy, filterValue) match {
      case (None, None) => None
      case (Some(by), Some(value)) =>
        try {
          Some(Filter(MlProposalFilterBy.withName(by), value))
        } catch {
          case e: NoSuchElementException =>
            throw new BadQueryParameterException(
              "Can't create filter. by[%s], value[%s]"
                format (by, value))
        }
      case _ => throw new BadQueryParameterException(
        "Invalid filter. Please query filterBy and " +
        "filterValue at the same time.")
    }

  private def createPage(startPage: Long, count: Long) = {
    if (startPage <= 0)
      throw new BadQueryParameterException(
        "Invalid startPage value. [%d]" format startPage)
    if (count <= 0 | count > maxCount)
      throw new BadQueryParameterException(
        "Invalid count value. [%d]" format count)
    Page(startPage, count)
  }

  private def createSort(sortBy: Option[String],
      sortOrder: Option[String]):
      Option[Sort[MlProposalSortBy.type]] =
    (sortBy, sortOrder) match {
      case (None, None) => None
      case (Some(by), Some(order)) =>
        try {
          Some(Sort(MlProposalSortBy.withName(by),
            SortOrder.withName(order)))
        } catch {
          case e: NoSuchElementException =>
            throw new BadQueryParameterException(
              "Can't create sort. by[%s], order[%s]"
                format (by, order))
        }
      case _ => throw new BadQueryParameterException(
          "Invalid sort. Please query sortBy and sortOrder " +
          "at the same time.")
    }

  @Path("{id}")
  @GET
  def show() = {
    Response.serverError().build()
  }

  @Path("{id}")
  @PUT
  def update(@PathParam("id") id: String, requestBody: String) : Response = {
    val dto = parse(requestBody).extract[RequestDto]

    def stringToLong(str: String) =
	    try {
	      str.toLong
	    } catch {
	      case e:NumberFormatException => throw new BadQueryParameterException("illegal format")
	    }

    try {
	    if(mpService.update(stringToLong(id), dto.toDomain)){
	    	Response.noContent().build()
	    } else {
	    	Response.status(Status.NOT_FOUND).build()
	    }
    } catch {
      case e : BadQueryParameterException => Response.status(Status.BAD_REQUEST).build() // 400
      case e => Response.serverError().build()
    }
  }

   /**
   * ML登録申請情報を削除します。
   *
   * @param id ID
   * @return 200(OK) or 400(Bad Request)
   */
  @Path("{id}")
  @DELETE
  def delete(@PathParam("id") id: String): Response = { 
    try {
      val idOption = ResourceHelper.getLongParam(id, "id")
      if (!idOption.isDefined) {
        throw new BadQueryParameterException(
            "Id is null")
      }
      mpService.delete(idOption.get)
      Response.noContent().build()
    } catch {
      case e: ResourceNotFoundException => {
        logger.error(e)
        Response.status(Status.NOT_FOUND).build()
      }
      case e: BadQueryParameterException => {
        logger.error(e)
        Response.status(Status.BAD_REQUEST).build()
      }
    }
    
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
    comment: String) {

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
        Some(comment))
  }

  private def toDto(result: MlProposalSearchResult):
      SearchResultDto =
    SearchResultDto(
      result.totalResults, result.startIndex,
      result.itemsPerPage, result.mlProposals map toDto)

  private def toDto(mlp: MlProposal) =
    MlProposalDto(
      mlp.id, mlp.proposerName, mlp.proposerEmail,
      mlp.mlTitle, mlp.status.toString,
      mlp.archiveType map { _.toString } getOrElse "",
      mlp.archiveUrl map { _.toString } getOrElse "",
      mlp.comment getOrElse "",
      dateFormat.format(mlp.createdAt),
      dateFormat.format(mlp.updatedAt))
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
  updatedAt: String)

/**
 * ML登録申請検索結果の変換用オブジェクト
 */
case class SearchResultDto(
    totalResults: Long,
    startIndex: Long,
    itemsPerPage: Long,
    mlProposals: List[MlProposalDto]) {
  // for lift-json
  implicit val formats = DefaultFormats

  def toJson(): String = Serialization.write(this)
}
