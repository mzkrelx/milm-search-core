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
import java.net.URL
import org.milmsearch.core.domain.ML
import org.milmsearch.core.domain.MLArchiveType
import org.milmsearch.core.service.MLService
import org.milmsearch.core.test.util.DateUtil.newDateTime
import org.milmsearch.core.test.util.MockCreatable
import org.milmsearch.core.{ComponentRegistry => CR}
import org.scalamock.scalatest.MockFactory
import org.scalamock.ProxyMockFactory
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FeatureSpec
import org.scalatest.GivenWhenThen
import javax.ws.rs.GET
import javax.ws.rs.Path
import org.milmsearch.core.domain.Page
import org.milmsearch.core.domain.MLSearchResult
import org.milmsearch.core.domain.ML
import org.milmsearch.core.test.util.DateUtil

/**
 * MLResource のテスト
 */
class MLResourceSpec extends FeatureSpec
    with MockFactory with ProxyMockFactory
    with MockCreatable with ShouldMatchers with GivenWhenThen {

  feature("MLResource クラス") {

    scenario("存在するML情報の詳細を取得する") {
      given("存在するML情報の ID を用いて")
      val mlID = "1"
      val m = createMock[MLService] {
        _ expects 'find withArgs(mlID.toLong) returning Some(newSampleML)
      }

      CR.mlService.doWith(m) {
        when("/mls/{id} に GET リクエストをすると")
        val response = new MLResource().show(mlID)

        then("ステータスコードは 200 が返る")
        response.getStatus should equal (200)

        and("リクエストボディは検索した ML 情報の JSON 表現を返す")
        response.getEntity should equal (
          """{"id":1,
             |"title":"ML タイトル",
             |"archiveType":"mailman",
             |"archiveURL":"http://localhost/path/to/archive/",
             |"lastMailedAt":"2013-01-01T00:00:00+09:00",
             |"approvedAt":"2013-01-05T00:00:00+09:00"
             |}""".stripMargin.filter(_ != '\n'))
      }
    }

    scenario("存在しないML情報の詳細を取得する") {
      given("存在しないML情報の ID を用いて")
      val mlID = "0"
      val m = createMock[MLService] {
        _ expects 'find withArgs(mlID.toLong) returning None
      }

      CR.mlService.doWith(m) {
        when("/mls/{id} に GET リクエストをすると")
        val response = new MLResource().show(mlID)

        then("ステータスコードは 404 が返る")
        response.getStatus should equal (404)
      }
    }

    scenario("不正なIDでML情報の詳細を取得する") {
      given("アルファベットの ID を用いて")
      val mlID = "abc"
      val m = createMock[MLService] { x => () }

      CR.mlService.doWith(m) {
        when("/mls/{id} に GET リクエストをすると")
        val response = new MLResource().show(mlID)

        then("ステータスコードは 400 が返る")
        response.getStatus should equal (400)
      }
    }

    scenario("ML一覧を取得する") {
      // サービスメソッドの結果
      val result = MLSearchResult(
        totalResults = 10,
        startIndex   = 1,
        itemsPerPage = 10,
        items = (1 to 10) map { i => ML(
            i,
            "MLタイトル" + i,
            MLArchiveType.Mailman,
            new URL("http://localhost/path/to/archive/"),
            Some(newDateTime(2013, 1, 1)),
            newDateTime(2013, 1, 1))
          } toList)
      val m = createMock[MLService] {
        _ expects 'search withArgs(
          Page(1L, 10L),
          None, // sort
          None  // filter
        ) returning result
      }

      CR.mlService.doWith(m) {
        given("デフォルトの一覧条件を用いて")
        when("/mls に GET リクエストをすると")
        val response = new MLResource().list(
          filterBy    = null,
          filterValue = null,
          startPage   = null,
          count       = null,
          sortBy      = null,
          sortOrder   = null)

        then("ステータスコードは 200 が返る")
        response.getStatus should equal (200)

        and("リクエストボディは検索した ML 情報の JSON 表現を返す")
        response.getEntity should equal (
          """{
          |"totalResults":10,
          |"startIndex":1,
          |"itemsPerPage":10,
          |"items":[%s]
          |}""".stripMargin format (
            1 to 10 map { i =>
              """{
              |"id":%s,
              |"title":"MLタイトル%s",
              |"archiveType":"mailman",
              |"archiveURL":"http://localhost/path/to/archive/",
              |"lastMailedAt":"2013-01-01T00:00:00+09:00",
              |"approvedAt":"2013-01-01T00:00:00+09:00"
              |}""".stripMargin format (i, i)
            } mkString ",") replaceAll ("\n", "")
        )
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