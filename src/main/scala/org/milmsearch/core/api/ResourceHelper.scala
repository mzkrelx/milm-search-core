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
import javax.ws.rs.core.Response
import net.liftweb.common.Loggable
import org.milmsearch.core.domain.Page
import org.milmsearch.core.domain.Sort
import org.milmsearch.core.domain.SortOrder
import org.milmsearch.core.domain.SortByEnum
import org.milmsearch.core.domain.Sort
import org.milmsearch.core.domain.MLProposalSortBy
import org.milmsearch.core.domain.Filter
import org.milmsearch.core.domain.MLProposalFilterBy
import org.milmsearch.core.domain.FilterByEnum


object ResourceHelper extends Loggable {
  def getLongParam(param: String): Option[Long] =
    param match {
      case null => None
      case p =>
        try {
          Some(p.toLong)
        } catch {
            case e: NumberFormatException => throw new BadQueryParameterException(
              "[%s] is not numeric." format (param))
        }
    }

  def getBooleanParam(param: String): Option[Boolean] =
    param match {
      case null => None
      case p =>
        try {
          Some(p.toBoolean)
        } catch {
            case e: NumberFormatException => throw new BadQueryParameterException(
              "[%s] is not boolean." format (param))
        }
    }

  def ok(body: String) = Response.ok(body).build()

  def noContent = Response.noContent.build()

  def err400(logMsg: String): Response = {
    logger.warn(logMsg)
    Response.status(Response.Status.BAD_REQUEST).build()
  }

  def err404(logMsg: String): Response = {
    logger.warn(logMsg)
    Response.status(Response.Status.NOT_FOUND).build()
  }

  def createPage(startPage: Long, count: Long, maxCount: Long) = {
    if (startPage <= 0)
      throw new BadQueryParameterException(
        "Invalid startPage value. [%d]" format startPage)
    if (count <= 0 | count > maxCount)
      throw new BadQueryParameterException(
        "Invalid count value. [%d]" format count)
    Page(startPage, count)
  }

  def createSort[E <: SortByEnum](sortBy: Option[String],
      sortOrder: Option[String],
      toColumn: String => E#Value): Option[Sort[E]] =
    (sortBy, sortOrder) match {
      case (None, None) => None
      case (Some(by), Some(order)) =>
        try {
          Some(Sort(toColumn(by),
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

}