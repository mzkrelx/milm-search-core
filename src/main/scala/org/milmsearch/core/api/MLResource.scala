/*
 * MilmSearch is a mailing list searching system.
 *
 * Copyright (C) 2013 MilmSearch Project.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 *
 * You can contact MilmSearch Project at mailing list
 * milm-search-public@lists.sourceforge.jp.
 */
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
import net.liftweb.util.Helpers.tryo
import net.liftweb.json.DefaultFormats
import net.liftweb.json.Serialization
import net.liftweb.util.Helpers.asLong
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam
import org.milmsearch.core.domain.MLSortBy
import org.milmsearch.core.domain.Filter
import org.milmsearch.core.domain.MLFilterBy
import org.milmsearch.core.domain.SortOrder
import org.milmsearch.core.domain.Page
import ResourceHelper._
import org.milmsearch.core.domain.MLSortBy
import org.milmsearch.core.domain.MLSearchResult

/**
 * ML情報のAPIリソース
 */
@Path("/mls")
class MLResource extends Loggable with PageableResource {

  /** デフォルトのソート順序 */
  protected val defaultSortBy = MLSortBy.Title

  /** ML情報管理サービス */
  private def mlService = ComponentRegistry.mlService()

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
      Response.ok(toDto(mlService.search(
        createPage(
          getLongParam(startPage) getOrElse defaultStartPage,
          getLongParam(count) getOrElse defaultCount,
          maxCount),
        createSort[MLSortBy.type](Option(sortBy), Option(sortOrder),
          s => MLSortBy.withName(s)),
        createFilter[MLFilterBy.type](
          Option(filterBy),
          MLFilterBy,
          Option(filterValue))
      )).toJson).build()
    } catch {
      case e: BadQueryParameterException => {
        logger.error(e)
        Response.status(Response.Status.BAD_REQUEST).build()
      }
    }
  }

  @Path("{id}")
  @GET
  @Produces(Array("application/json"))
  def show(@PathParam("id") id: String): Response =
    asLong(id) match {
      case Full(longID) => mlService.find(longID) match {
        case None => err404("ML Not Found. id=[%s]" format longID)
        case Some(ml) => ok(toDto(ml).toJson)
      }
      case _ => err400("Invalid path param. id=[%s]" format id)
    }

  /**
   * ML一覧・検索結果を JSON 変換用オブジェクトに変換する
   */
  private def toDto(result: MLSearchResult):
      SearchResultDto[MLDto] =
    SearchResultDto(
      result.totalResults, result.startIndex,
      result.itemsPerPage, result.items map toDto)

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
