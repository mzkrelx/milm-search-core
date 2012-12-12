package org.milmsearch.core.api
import java.net.URI
import java.net.URL

import org.milmsearch.core.domain.CreateMlProposalRequest
import org.milmsearch.core.domain.MlArchiveType
import org.milmsearch.core.domain.MlProposalStatus
import org.milmsearch.core.ComponentRegistry

import javax.ws.rs.core.Response.Status
import javax.ws.rs.core.Response
import javax.ws.rs.Consumes
import javax.ws.rs.DELETE
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.PUT
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import net.liftweb.json.parse
import net.liftweb.json.DefaultFormats

class BadRequestException extends Exception

/**
 * ML登録申請情報のAPIリソース
 */
@Path("/ml-proposals")
class MlProposalResource {
  // for lift-json
  implicit val formats = DefaultFormats

  /** ML登録申請管理サービス */
  private def mpService = ComponentRegistry.mlProposalService.vend

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

  @GET
  def list() = {
    Response.serverError().build()
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
	      case e:NumberFormatException => throw new BadRequestException
	    }

    try {
	    if(mpService.update(stringToLong(id), dto.toDomain)){
	    	Response.noContent().build()
	    } else {
	    	Response.status(Status.NOT_FOUND).build()
	    }
    } catch {
      case e : BadRequestException => Response.status(Status.BAD_REQUEST).build() // 400
      case e => Response.serverError().build()
    }
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
}
