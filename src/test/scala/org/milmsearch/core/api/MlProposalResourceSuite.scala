package org.milmsearch.core.api
import java.net.URI
import java.net.URL
import org.milmsearch.core.domain.CreateMlProposalRequest
import org.milmsearch.core.domain.MlArchiveType
import org.milmsearch.core.domain.MlProposal
import org.milmsearch.core.domain.MlProposalStatus
import org.milmsearch.core.service.MlProposalService
import org.scalamock.scalatest.MockFactory
import org.scalamock.ProxyMockFactory
import org.scalatest.FunSuite
import org.milmsearch.core.ComponentRegistry
import java.util.Date

class MlProposalResourceSuite extends FunSuite
    with MockFactory with ProxyMockFactory {

  test("create full") {
    val json = """
      |{
      |  "proposerName": "申請者の名前",
      |  "proposerEmail": "proposer@example.com",
      |  "mlTitle": "MLタイトル",
      |  "status": "new",
      |  "archiveType": "mailman",
      |  "archiveUrl": "http://localhost/path/to/archive/",
      |  "comment": "コメント(MLの説明など)"
      |}""".stripMargin

    val request = CreateMlProposalRequest(
      "申請者の名前",
      "proposer@example.com",
      "MLタイトル",
      MlProposalStatus.New,
      Some(MlArchiveType.Mailman),
      Some(new URL("http://localhost/path/to/archive/")),
      Some("コメント(MLの説明など)")
    )

    val m = mock[MlProposalService]
    m expects 'create withArgs(request) returning 1L

    val response = ComponentRegistry.mlProposalService.doWith(m) {
      new MlProposalResource().create(json)
    }

    expect(201) { response.getStatus }
    expect(new URI("/ml-proposal/1")) {
      response.getMetadata().getFirst("Location")
    }
  }

  test("update full") {
    val json = """
      |{
      |  "proposerName": "申請者の名前",
      |  "proposerEmail": "proposer@example.com",
      |  "mlTitle": "MLタイトル",
      |  "status": "new",
      |  "archiveType": "mailman",
      |  "archiveUrl": "http://localhost/path/to/archive/",
      |  "comment": "コメント(MLの説明など)"
      |}""".stripMargin

      val request = CreateMlProposalRequest(
      "申請者の名前",
      "proposer@example.com",
      "MLタイトル",
      MlProposalStatus.New,
      Some(MlArchiveType.Mailman),
      Some(new URL("http://localhost/path/to/archive/")),
      Some("コメント(MLの説明など)")
    )

    val id = "1": String
    val m = mock[MlProposalService]
    m expects 'update withArgs(id.toLong, request) returning true

    val response = ComponentRegistry.mlProposalService.doWith(m) {
      new MlProposalResource().update(id, json)
    }

    expect(204) { response.getStatus }
  }

  test("update notfound") {
    val json = """
      |{
      |  "proposerName": "申請者の名前",
      |  "proposerEmail": "proposer@example.com",
      |  "mlTitle": "MLタイトル",
      |  "status": "new",
      |  "archiveType": "mailman",
      |  "archiveUrl": "http://localhost/path/to/archive/",
      |  "comment": "コメント(MLの説明など)"
      |}""".stripMargin

      val request = CreateMlProposalRequest(
      "申請者の名前",
      "proposer@example.com",
      "MLタイトル",
      MlProposalStatus.New,
      Some(MlArchiveType.Mailman),
      Some(new URL("http://localhost/path/to/archive/")),
      Some("コメント(MLの説明など)")
    )

    val id = "1": String
    val m = mock[MlProposalService]
    m expects 'update withArgs(id.toLong, request) returning false

    val response = ComponentRegistry.mlProposalService.doWith(m) {
      new MlProposalResource().update(id, json)
    }

    expect(404) { response.getStatus }
  }

  test("update id illegal format") {
    val json = """
      |{
      |  "proposerName": "申請者の名前",
      |  "proposerEmail": "proposer@example.com",
      |  "mlTitle": "MLタイトル",
      |  "status": "new",
      |  "archiveType": "mailman",
      |  "archiveUrl": "http://localhost/path/to/archive/",
      |  "comment": "コメント(MLの説明など)"
      |}""".stripMargin

      val request = CreateMlProposalRequest(
      "申請者の名前",
      "proposer@example.com",
      "MLタイトル",
      MlProposalStatus.New,
      Some(MlArchiveType.Mailman),
      Some(new URL("http://localhost/path/to/archive/")),
      Some("コメント(MLの説明など)")
    )

    val id = "one": String
    val response = new MlProposalResource().update(id, json)
    expect(400) { response.getStatus }
  }

}