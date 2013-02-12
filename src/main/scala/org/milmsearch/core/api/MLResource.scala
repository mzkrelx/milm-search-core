package org.milmsearch.core.api
import org.milmsearch.common.DateUtil.formatToISO
import org.milmsearch.core.domain.ML
import org.milmsearch.core.ComponentRegistry

import ResourceHelper.err400
import ResourceHelper.err404
import ResourceHelper.ok
import javax.ws.rs.core.Response
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import net.liftweb.common.Full
import net.liftweb.common.Loggable
import net.liftweb.json.DefaultFormats
import net.liftweb.json.Serialization
import net.liftweb.util.Helpers.asLong

/**
 * ML情報のAPIリソース
 */
@Path("/mls")
class MLResource extends Loggable {

  /** ML情報管理サービス */
  private def mlService = ComponentRegistry.mlService()

  @Path("{id}")
  @GET
  def show(@PathParam("id") id: String): Response =
    asLong(id) match {
      case Full(longID) => mlService.find(longID) match {
        case None => err404("ML Not Found. [id=%s]" format longID)
        case Some(ml) => ok(toDto(ml).toJson)
      }
      case _ => err400("Invalid path param. [id=%s]" format id)
    }

  /**
   * ML情報を JSON 変換用オブジェクトに変換する
   */
  private def toDto(ml: ML) =
    MLDto(
      id           = ml.id,
      title        = ml.title,
      archiveType  = ml.archiveType.toString,
      archiveURL   = ml.archiveURL.toString,
      lastMailedAt = formatToISO(ml.lastMailedAt),
      approvedAt   = formatToISO(ml.approvedAt))
}

/**
 * ML情報の JSON 変換用オブジェクト
 */
case class MLDto(
    id: Long,
    title: String,
    archiveType: String,
    archiveURL: String,
    lastMailedAt: String,
    approvedAt: String) {
  def toJson = Serialization.write(this)(DefaultFormats)
}
