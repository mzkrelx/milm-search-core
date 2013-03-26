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
import java.net.URL
import org.milmsearch.core.dao.MLProposalDao
import org.milmsearch.core.domain.MLArchiveType
import org.milmsearch.core.domain.ML
import org.milmsearch.core.domain.{CreateMLProposalRequest => CreateMLPRequest}
import org.milmsearch.core.domain.MLProposalStatus
import org.milmsearch.core.{ComponentRegistry => CR}
import org.scalamock.scalatest.MockFactory
import org.scalamock.ProxyMockFactory
import org.scalatest.FeatureSpec
import org.milmsearch.core.test.util.MockCreatable
import org.scalatest.matchers.ShouldMatchers
import org.milmsearch.core.dao.MLDao
import java.util.Date
import org.joda.time.DateTime
import org.milmsearch.core.test.util.DateUtil._
import org.scalatest.GivenWhenThen

/**
 * MLService のテスト
 */
class MLServiceSpec extends FeatureSpec
    with MockFactory with ProxyMockFactory
    with MockCreatable with ShouldMatchers with GivenWhenThen {

  feature("MLService クラス") {

    scenario("存在するML情報を検索する") {
      given("存在するML情報の ID を引数に")
      val mlID = 1L
      val m = createMock[MLDao] {
        _ expects 'find withArgs(mlID) returning Some(newSampleML)
      }

      when("find メソッドを呼び出した時に")
      then("Some(検索したML情報) を返す")
      CR.mlDao.doWith(m) {
        new MLServiceImpl().find(mlID) should equal (Some(newSampleML))
      }
    }

    scenario("存在しないML情報を検索する") {
      given("存在しないML情報の ID を引数に")
      val mlID = 0L
      val m = createMock[MLDao] {
        _ expects 'find withArgs(mlID) returning None
      }

      when("find メソッドを呼び出した時に")
      then("None を返す")
      CR.mlDao.doWith(m) {
        new MLServiceImpl().find(mlID) should equal (None)
      }
    }

  }

  /**
   * サンプルML情報を生成する
   */
  private def newSampleML = ML(
    id           = 1L,
    title        = "ML タイトル",
    archiveType  = MLArchiveType.Mailman,
    archiveURL   = new URL("http://localhost/path/to/archive/"),
    lastMailedAt = Some(newDateTime(2013, 1, 1)),
    approvedAt   = newDateTime(2013, 1, 5))
}