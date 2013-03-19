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
import java.net.URI
import java.net.URL
import org.milmsearch.core.domain.MLArchiveType
import org.milmsearch.core.domain.{CreateMLProposalRequest => CreateMLPRequest}
import org.milmsearch.core.domain.MLProposalStatus
import org.milmsearch.core.service.MLProposalService
import org.milmsearch.core.ComponentRegistry.{mlProposalService => mlpService}
import org.scalamock.scalatest.MockFactory
import org.scalamock.ProxyMockFactory
import org.scalatest.FunSpec
import org.milmsearch.core.test.util.MockCreatable
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.GivenWhenThen

class MLProposalResourceSpec extends FunSpec
    with MockFactory with ProxyMockFactory
    with MockCreatable with ShouldMatchers with GivenWhenThen {

  describe("ML 申請情報作成 API") {
    it("""リクエストボディ の JSON を作成要求ドメインに変換して Service に渡し、
    	 |  作成結果をレスポンスとして返す。""".stripMargin) {

      given("任意項目も含めて全ての項目が存在し、かつ妥当な JSON")
      val json = """
        |{
        |  "proposerName": "申請者の名前",
        |  "proposerEmail": "proposer@example.com",
        |  "mlTitle": "MLタイトル",
        |  "status": "new",
        |  "archiveType": "mailman",
        |  "archiveURL": "http://localhost/path/to/archive/",
        |  "comment": "コメント(MLの説明など)"
        |}""".stripMargin

      when("POST リクエストを受け付けると")
      then("JSON を作成要求ドメインに変換して Service に渡す")
      val m = createMock[MLProposalService] {
        _ expects 'create withArgs(
          CreateMLPRequest(
            "申請者の名前",
            "proposer@example.com",
            "MLタイトル",
            MLProposalStatus.New,
            Some(MLArchiveType.Mailman),
            Some(new URL("http://localhost/path/to/archive/")),
            Some("コメント(MLの説明など)")
          )) returning 1L
      }
      val response = mlpService.doWith(m) {
        new MLProposalResource().create(json)
      }

      then("ステータスコードは 201 を返す")
      response.getStatus should equal (201)

      then("Location ヘッダーに作成したリソースの URL を記載する")
      response.getMetadata.getFirst("Location") should
        equal (new URI("/ml-proposals/1"))
    }

    it ("申請ステータスの値は new のみ許可する。") {
      given("status が accepted の JSON")
      val json = """
        |{
        |  "proposerName": "申請者の名前",
        |  "proposerEmail": "proposer@example.com",
        |  "mlTitle": "MLタイトル",
        |  "status": "accepted",
        |  "archiveType": "mailman",
        |  "archiveURL": "http://localhost/path/to/archive/",
        |  "comment": "コメント(MLの説明など)"
        |}""".stripMargin

      when("POST リクエストを受け付けると")
      then("Service のメソッドは呼ばずに、例外を投げる")
      val m = createMock[MLProposalService] { x => () }
      mlpService.doWith(m) {
        evaluating {
          new MLProposalResource().create(json)
        } should produce [BadRequestException]
      }
    }

    it ("必須項目が JSON に含まれていない場合は BadRequestException を投げる。") {
      given("archiveType, archiveURL, comment が存在しない JSON")
      val json = """
        |{
        |  "proposerName": "申請者の名前",
        |  "proposerEmail": "proposer@example.com",
        |  "mlTitle": "MLタイトル",
        |  "status": "new",
        |}""".stripMargin

      when("POST リクエストを受け付けると")
      then("Service のメソッドは呼ばずに、例外を投げる")
      val m = createMock[MLProposalService] { x => () }
      mlpService.doWith(m) {
        evaluating {
          new MLProposalResource().create(json)
        } should produce [BadRequestException]
      }
    }

    it ("リクエストボディが空の場合は BadRequestException を投げる。") {
      given("空文字列")
      val json = ""

      when("POST リクエストを受け付けると")
      then("Service のメソッドは呼ばずに、例外を投げる")
      val m = createMock[MLProposalService] { x => () }
      mlpService.doWith(m) {
        evaluating {
          new MLProposalResource().create(json)
        } should produce [BadRequestException]
      }
    }

  }
}