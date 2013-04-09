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
package org.milmsearch.core.service

import org.milmsearch.core.domain.CreateMLProposalRequest
import org.milmsearch.core.domain.Filter
import org.milmsearch.core.domain.MLProposal
import org.milmsearch.core.domain.{MLProposalFilterBy => MLPFilterBy}
import org.milmsearch.core.domain.{MLProposalSearchResult => MLPSearchResult}
import org.milmsearch.core.domain.{MLProposalSortBy => MLPSortBy}
import org.milmsearch.core.domain.Page
import org.milmsearch.core.domain.Sort
import org.milmsearch.core.exception.DeleteFailedException
import org.milmsearch.core.exception.ResourceNotFoundException
import org.milmsearch.core.ComponentRegistry
import net.liftweb.common.Loggable
import org.milmsearch.core.domain.MLProposalColumn
import org.milmsearch.core.domain.MLProposalStatus
import org.milmsearch.core.domain.CreateMLRequest
import org.milmsearch.core.domain.UpdateMLProposalRequest

/**
 * ML登録申請情報を管理するサービス
 */
trait MLProposalService {

  /**
   * ML登録申請情報を作成する
   *
   * @param mlProposal ML登録申請情報
   * @return ID
   */
  def create(request: CreateMLProposalRequest): Long

  /**
   * 検索結果情報を取得する
   *
   * @param filter 検索条件
   * @param page   取得するページ番号と1ページあたりの件数
   * @param sort   ソート方法
   * @return 検索結果情報
   */
  def search(page: Page,
      sort: Option[Sort[MLPSortBy.type]] = None,
      filter: Option[Filter[MLPFilterBy.type]] = None):
      MLPSearchResult

  /**
   * ML登録申請情報を取得する
   *
   * @param id ID
   * @return ML登録申請情報
   */
  def find(id: Long): Option[MLProposal]

  /**
   * ML登録申請情報を更新する
   *
   * @param id ID
   * @param updateRequest ML登録申請情報
   */
  @throws(classOf[ResourceNotFoundException])
  def update(id: Long, updateRequest: UpdateMLProposalRequest)

  /**
   * ML登録申請情報を削除する
   *
   * @param id ID
   */
  @throws(classOf[ResourceNotFoundException])
  @throws(classOf[DeleteFailedException])
  def delete(id: Long)

  /**
   * ML登録申請情報を承認する
   *
   * @param id ID
   * @throws ResourceNotFoundException
   */
  def accept(id: Long)

  /**
   * ML登録申請情報を却下する
   *
   * @param id ID
   * @throws ResourceNotFoundException
   */
  def reject(id: Long)

}

/**
 * 検索に失敗したときの例外
 */
class SearchFailedException(msg: String) extends Exception(msg)

/**
 * MLProposalService の実装クラス
 */
class MLProposalServiceImpl extends MLProposalService with Loggable {

  /** ML登録申請情報 DAO */
  private def mpDao = ComponentRegistry.mlProposalDao()

  /** ML情報サービス */
  private def mlService = ComponentRegistry.mlService()

  /** 日付サービス */
  private def dateTimeService = ComponentRegistry.dateTimeService()

  def create(request: CreateMLProposalRequest) =
    mpDao.create(request)

  def search(page: Page,
      sort: Option[Sort[MLPSortBy.type]] = None,
      filter: Option[Filter[MLPFilterBy.type]] = None) = {
    val mlProposals = mpDao.findAll(page.toRange, sort, filter)
    MLPSearchResult(
      mpDao.count(filter),
      page.getStartIndex,
      mlProposals.length.toLong min page.count, mlProposals)
  }

  def find(id: Long) = mpDao.find(id)

  def update(id: Long, updateRequest: UpdateMLProposalRequest) {
    def updateColValList = {
      import MLProposalColumn._
      import updateRequest._
      List((MLTitle, mlTitle),
        (ArchiveType, archiveType),
        (ArchiveURL, archiveURL))
    }

    if (!mpDao.update(id, updateColValList)) {
      throw new ResourceNotFoundException(
        "MLProposal to update is not found. id=[%s]" format id)
    }
  }

  def delete(id: Long) {
    try {
      val isDeleted = mpDao.delete(id)
      if (! isDeleted) {
        throw new ResourceNotFoundException("Not found.")
      }
    } catch {
      case e: ResourceNotFoundException => {
      	logger.error(e)
      	throw e
      }
      case e => {
        logger.error(e)
        throw new DeleteFailedException(
          "Delete failed.")
      }
    }
  }

  def accept(id: Long) {
    val now = dateTimeService.now
    if (!mpDao.update(id, List(
        (MLProposalColumn.Status, MLProposalStatus.Accepted),
        (MLProposalColumn.JudgedAt, now.toDate)))) {
      throw new ResourceNotFoundException(
        "MLProposal to accept is not found.")
    }

    find(id) match {
      case None => throw new ResourceNotFoundException(
        "ML Proposal is not found. [id=%s]" format id)
      case Some(mlp) =>
        mlService.create(
          CreateMLRequest(
            mlp.mlTitle, mlp.archiveType.get, mlp.archiveURL.get, now))
    }
  }

  def reject(id: Long) {
    if (!mpDao.update(id, List(
        (MLProposalColumn.Status, MLProposalStatus.Rejected),
        (MLProposalColumn.JudgedAt, dateTimeService.now.toDate)))) {
      throw new ResourceNotFoundException(
        "MLProposal to reject is not found.")
    }
  }
}
