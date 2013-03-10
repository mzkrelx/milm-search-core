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
import org.milmsearch.core.ComponentRegistry
import org.milmsearch.core.domain.CreateMLProposalRequest
import org.milmsearch.core.exception.ResourceNotFoundException
import org.milmsearch.core.domain.MLProposal
import org.milmsearch.core.exception.DeleteFailedException
import org.milmsearch.core.domain.ML
import org.milmsearch.core.domain.CreateMLRequest
import org.milmsearch.core.domain.MLSearchResult
import org.milmsearch.core.domain.Page
import net.liftweb.common.Loggable
import org.milmsearch.core.domain.Filter
import org.milmsearch.core.domain.Sort
import org.milmsearch.core.domain.MLSortBy
import org.milmsearch.core.domain.MLFilterBy
import org.milmsearch.core.domain.MLSearchResult

/**
 * ML情報を管理するサービス
 */
trait MLService {

  /**
   * ML情報を作成する
   *
   * @param req ML情報の作成要求
   * @return ID
   */
  def create(req: CreateMLRequest): Long

  /**
   * MLの検索結果情報を取得する
   *
   * @param filter 検索条件
   * @param page   取得するページ番号と1ページあたりの件数
   * @param sort   ソート方法
   * @return 検索結果情報
   */
  def search(page: Page,
      sort: Option[Sort[MLSortBy.type]] = None,
      filter: Option[Filter[MLFilterBy.type]] = None): MLSearchResult

  /**
   * ML情報を取得する
   *
   * @param id ID
   * @return ML情報
   */
  def find(id: Long): Option[ML]
}

/**
 * MLService の実装クラス
 */
class MLServiceImpl extends MLService with Loggable {

  /** ML情報 DAO */
  private def mlDao = ComponentRegistry.mlDao()

  def create(req: CreateMLRequest) = mlDao.create(req)

  def search(page: Page,
      sort: Option[Sort[MLSortBy.type]] = None,
      filter: Option[Filter[MLFilterBy.type]] = None) = {
    val mls = mlDao.findAll(page.toRange, sort, filter)
    MLSearchResult(
      mlDao.count(filter),
      page.getStartIndex,
      mls.length.toLong min page.count, mls)
  }

  def find(id: Long) = mlDao.find(id)
}
