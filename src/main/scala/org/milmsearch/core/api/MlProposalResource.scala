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
import net.liftweb.json.DefaultFormats
import net.liftweb.json.Serialization
import net.liftweb.json.parse
import org.milmsearch.core.domain.Sort
import javax.ws.rs.PathParam
import javax.ws.rs.core.Response.Status
import org.milmsearch.core.exception.ResourceNotFoundException
import net.liftweb.json.MappingException
import ResourceHelper._
import org.milmsearch.core.domain.UpdateMlProposalRequest

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
   *   "archiveURL": "メールアーカイブの基底URL",
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
    val dto = try {
      parse(requestBody).extract[CreateRequestDto]
    } catch {
      case e: MappingException => {
        logger.warn(e)
        throw new BadRequestException("invalid json format")
      }
    }

    if (dto.status != "new") {
      throw new BadRequestException("status is allowed only 'new'")
    }

    val id = mpService.create(dto.toDomain)

    Response.created(new URI("/ml-proposal/" + id)).build()
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
          getLongParam(startPage) getOrElse defaultStartPage,
          getLongParam(count) getOrElse defaultCount),
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

  //  TODO ResourceHelper に移動したので消す
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
  def show(@PathParam("id")id: String) = {
    try {
      getLongParam(id) match {
        case None => err400("Param 'id' is not passed.")
        case Some(id) => mpService.find(id) match {
          case None => err404("Not Found.")
          case Some(mlp) => ok(toDto(mlp).toJson)
        }
      }
    } catch {
      case e: BadQueryParameterException => err400(e.getMessage)
    }
  }

  /**
   * ML登録申請情報を更新します。
   *
   * @param id
   * @param requestBody
   */
  @Path("{id}")
  @PUT
  def update(@PathParam("id") id: String, requestBody: String) = {
    try {
      getLongParam(id) match {
        case None => err400("Param 'id' is not passed.")
        case Some(x) => {
          val dto = try {
            parse(requestBody).extract[UpdateRequestDto]
          } catch {
            case e: MappingException => {
              logger.warn(e)
              throw new BadRequestException("invalid json format")
            }
          }
          mpService.update(x, dto.toDomain)
          noContent
        }
      }
    } catch {
      case e @ (_: BadQueryParameterException |
                _: BadRequestException) => err400(e.getMessage)
      case e: ResourceNotFoundException => err404(e.getMessage)
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
      val idOption = getLongParam(id)
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
   * ML登録申請情報を削除します。
   *
   * @param id ID
   * @param isAcception true は承認、false は却下
   * @return 200(OK) or 400(Bad Request) or 404(Not Found)
   * @TODO トランザクション処理
   */
  @Path("{id}")
  @POST
  def accept(@PathParam("id") id: String,
             @QueryParam("accept")isAccepting: String) = {
    try {
      getLongParam(id) match {
        case None => err400("Param 'id' is not passed.")
        case Some(i) => {
          getBooleanParam(isAccepting) match {
            case None => err400("Param 'accept' is not passed.")
            case Some(true)  => {
              mpService.accept(i)
              Response.noContent().build() // TODO 仕様未定
            }
            case Some(false) => {
              mpService.reject(i)
              Response.noContent().build() // TODO 仕様未定
            }
          }
        }
      }
    } catch {
      case e: BadQueryParameterException => err400(e.getMessage)
      case e: ResourceNotFoundException  => err404(e.getMessage)
    }
  }

  /**
   * 新規申請時リクエストボディの変換用オブジェクト
   */
  case class CreateRequestDto(
    proposerName: String,
    proposerEmail: String,
    mlTitle: String,
    status: String,
    archiveType: String,
    archiveURL: String,
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
        Some(new URL(archiveURL)),
        Some(comment))
  }

  /**
   * 更新時リクエストボディの変換用オブジェクト
   */
  case class UpdateRequestDto(
    mlTitle: String,
    archiveType: String,
    archiveURL: String) {

    def toDomain =
      UpdateMlProposalRequest(
        mlTitle,
        MlArchiveType.withName(archiveType),
        new URL(archiveURL))
  }

  private def toDto(result: MlProposalSearchResult):
      SearchResultDto[MlProposalDto] =
    SearchResultDto(
      result.totalResults, result.startIndex,
      result.itemsPerPage, result.mlProposals map toDto)

  private def toDto(mlp: MlProposal) =
    MlProposalDto(
      mlp.id, mlp.proposerName, mlp.proposerEmail,
      mlp.mlTitle, mlp.status.toString,
      mlp.archiveType map { _.toString } getOrElse "",
      mlp.archiveURL map { _.toString } getOrElse "",
      mlp.comment getOrElse "",
      dateFormat.format(mlp.createdAt),
      dateFormat.format(mlp.updatedAt),
      mlp.judgedAt map { dateFormat.format(_) } getOrElse "")
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
  archiveURL: String,
  comment: String,
  createdAt: String,
  updatedAt: String,
  judgedAt: String) {
  // for lift-json
  implicit val formats = DefaultFormats

  def toJson(): String = Serialization.write(this)
}
