package org.milmsearch.core.api
import java.net.URI
import java.net.URL
import org.milmsearch.core.domain.MlArchiveType
import org.milmsearch.core.domain.{CreateMlProposalRequest => CreateMLPRequest}
import org.milmsearch.core.domain.MlProposalStatus
import org.milmsearch.core.service.MlProposalService
import org.milmsearch.core.ComponentRegistry.{mlProposalService => mlpService}
import org.scalamock.scalatest.MockFactory
import org.scalamock.ProxyMockFactory
import org.scalatest.FunSpec
import org.milmsearch.core.test.util.MockCreatable
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.GivenWhenThen

class MlProposalResourceSpec extends FunSpec
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
      val m = createMock[MlProposalService] {
        _ expects 'create withArgs(
          CreateMLPRequest(
            "申請者の名前",
            "proposer@example.com",
            "MLタイトル",
            MlProposalStatus.New,
            Some(MlArchiveType.Mailman),
            Some(new URL("http://localhost/path/to/archive/")),
            Some("コメント(MLの説明など)")
          )) returning 1L
      }
      val response = mlpService.doWith(m) {
        new MlProposalResource().create(json)
      }

      then("ステータスコードは 201 を返す")
      response.getStatus should equal (201)

      then("Location ヘッダーに作成したリソースの URL を記載する")
      response.getMetadata.getFirst("Location") should
        equal (new URI("/ml-proposal/1"))
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
      val m = createMock[MlProposalService] { x => () }
      mlpService.doWith(m) {
        evaluating {
          new MlProposalResource().create(json)
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
      val m = createMock[MlProposalService] { x => () }
      mlpService.doWith(m) {
        evaluating {
          new MlProposalResource().create(json)
        } should produce [BadRequestException]
      }
    }

    it ("リクエストボディが空の場合は BadRequestException を投げる。") {
      given("空文字列")
      val json = ""

      when("POST リクエストを受け付けると")
      then("Service のメソッドは呼ばずに、例外を投げる")
      val m = createMock[MlProposalService] { x => () }
      mlpService.doWith(m) {
        evaluating {
          new MlProposalResource().create(json)
        } should produce [BadRequestException]
      }
    }

  }
}