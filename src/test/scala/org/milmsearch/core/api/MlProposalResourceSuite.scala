package org.milmsearch.core.api
import java.net.URI
import java.net.URL
import org.milmsearch.core.domain.MlArchiveType
import org.milmsearch.core.domain.CreateMlProposalRequest
import org.milmsearch.core.domain.MlProposalStatus
import org.milmsearch.core.service.MlProposalService
import org.milmsearch.core.ComponentRegistry
import org.scalamock.scalatest.MockFactory
import org.scalamock.ProxyMockFactory
import org.scalatest.FunSuite
import org.milmsearch.core.domain.Filter
import org.milmsearch.core.domain.Sort
import org.milmsearch.core.domain.SortOrder
import org.milmsearch.core.domain.Page
import org.milmsearch.core.domain.MlProposal
import java.util.Date
import java.util.Calendar
import java.text.SimpleDateFormat
import org.milmsearch.core.domain.MlProposalSearchResult
import org.apache.commons.lang3.time.DateUtils
import org.milmsearch.core.domain.{MlProposalFilterBy => MLPFilterBy}
import org.milmsearch.core.domain.{MlProposalSortBy => MLPSortBy}

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

  test("get by all params") {
    val m = mock[MlProposalService]
    val cal = Calendar.getInstance()
    cal.set(2012, Calendar.OCTOBER, 28, 10, 20, 30)
    val createdAt = cal.getTime()
    val mlProposals = for (i <- 21 to 40) yield MlProposal(
      i,
      "申請者の名前",
      "proposer@example.com",
      "MLタイトル" + i,
      MlProposalStatus.New,
      Some(MlArchiveType.Mailman),
      Some(new URL("http://localhost/path/to/archive/")),
      Some("コメント(MLの説明など)"),
      createdAt,
      createdAt
    )
    
    m expects 'search withArgs(
      Filter(MLPFilterBy.Status, "new"),
      Page(2, 20),
      Sort(MLPSortBy.ArchiveType, SortOrder.Ascending)
    ) returning MlProposalSearchResult(100, 21, 20, mlProposals.toList)

    val response = ComponentRegistry.mlProposalService.doWith(m) {
      new MlProposalResource().list("status", "new", "archiveType", "ascending", "2", "20")
    }
    expect(200) { response.getStatus() }

    val json = """{
      |"totalResults":100,
      |"startIndex":21,
      |"itemsPerPage":20,
      |"mlProposals":[{
        |"id":21,
        |"proposerName":"申請者の名前",
        |"proposerEmail":"proposer@example.com",
        |"mlTitle":"MLタイトル21",
        |"status":"new",
        |"archiveType":"mailman",
        |"archiveUrl":"http://localhost/path/to/archive/",
        |"comment":"コメント(MLの説明など)",
        |"createdAt":"2012-10-28T10:20:30",
        |"updatedAt":"2012-10-28T10:20:30"
      |}""".stripMargin.replaceAll("\n", "")
      
    expect(json) {
      response.getEntity.toString.substring(0, json.length)
    }
  }

  test("get by default params") {
    val m = mock[MlProposalService]
    val cal = Calendar.getInstance()
    cal.set(2012, Calendar.OCTOBER, 28, 10, 20, 30)
    val createdAt = cal.getTime()
    val mlProposals = for (i <- 1 to 10) yield MlProposal(
      i,
      "申請者の名前",
      "proposer@example.com",
      "MLタイトル" + i,
      MlProposalStatus.New,
      Some(MlArchiveType.Mailman),
      Some(new URL("http://localhost/path/to/archive/")),
      Some("コメント(MLの説明など)"),
      createdAt,
      createdAt
    )
    
    m expects 'search withArgs(
      Page(1, 10),
      Sort(MLPSortBy.MlTitle, SortOrder.Ascending)
    ) returning MlProposalSearchResult(100, 1, 10, mlProposals.toList)

    val response = ComponentRegistry.mlProposalService.doWith(m) {
      new MlProposalResource().list(null, null, null, null, null, null)
    }
    expect(200) { response.getStatus() }

    val json = """{
      |"totalResults":100,
      |"startIndex":1,
      |"itemsPerPage":10,
      |"mlProposals":[{
        |"id":1,
        |"proposerName":"申請者の名前",
        |"proposerEmail":"proposer@example.com",
        |"mlTitle":"MLタイトル1",
        |"status":"new",
        |"archiveType":"mailman",
        |"archiveUrl":"http://localhost/path/to/archive/",
        |"comment":"コメント(MLの説明など)",
        |"createdAt":"2012-10-28T10:20:30",
        |"updatedAt":"2012-10-28T10:20:30"
      |}""".stripMargin.replaceAll("\n", "")
      
    expect(json) {
      response.getEntity.toString.substring(0, json.length)
    }
  }

  test("get when filterBy is null") {
    val response = new MlProposalResource().list(null, "new", "mlTitle", "ascending", "2", "20")
    expect(400) { response.getStatus() }
  }
  
  test("get when filterBy is invalid") {
    val response = new MlProposalResource().list("hello", "new", "mlTitle", "ascending", "2", "20")
    expect(400) { response.getStatus() }
  }
  
  test("get when filterValue is null") {
    val response = new MlProposalResource().list("status", null, "mlTitle", "ascending", "2", "20")
    expect(400) { response.getStatus() }
  }
  
  test("get when sortBy is null") {
    val m = mock[MlProposalService]
    val cal = Calendar.getInstance()
    cal.set(2012, Calendar.OCTOBER, 28, 10, 20, 30)
    val createdAt = cal.getTime()
    val mlProposals = for (i <- 21 to 40) yield MlProposal(
      i,
      "申請者の名前",
      "proposer@example.com",
      "MLタイトル" + i,
      MlProposalStatus.New,
      Some(MlArchiveType.Mailman),
      Some(new URL("http://localhost/path/to/archive/")),
      Some("コメント(MLの説明など)"),
      createdAt,
      createdAt
    )
    
    m expects 'search withArgs(
      Filter(MLPFilterBy.Status, "new"),
      Page(2, 20),
      Sort(MLPSortBy.MlTitle, SortOrder.Ascending)
    ) returning MlProposalSearchResult(100, 21, 20, mlProposals.toList)

    val response = ComponentRegistry.mlProposalService.doWith(m) {
      new MlProposalResource().list("status", "new", null, "ascending", "2", "20")
    }
    expect(200) { response.getStatus() }

    val json = """{
      |"totalResults":100,
      |"startIndex":21,
      |"itemsPerPage":20,
      |"mlProposals":[{
        |"id":21,
        |"proposerName":"申請者の名前",
        |"proposerEmail":"proposer@example.com",
        |"mlTitle":"MLタイトル21",
        |"status":"new",
        |"archiveType":"mailman",
        |"archiveUrl":"http://localhost/path/to/archive/",
        |"comment":"コメント(MLの説明など)",
        |"createdAt":"2012-10-28T10:20:30",
        |"updatedAt":"2012-10-28T10:20:30"
      |}""".stripMargin.replaceAll("\n", "")
      
    expect(json) {
      response.getEntity.toString.substring(0, json.length)
    }
  }
  
  test("get when sortBy is invalid") {
    val response = new MlProposalResource().list("status", "new", "hello", "ascending", "2", "20")
    expect(400) { response.getStatus() }
  }
  
  test("get when sortOrder is null") {
    val m = mock[MlProposalService]
    val cal = Calendar.getInstance()
    cal.set(2012, Calendar.OCTOBER, 28, 10, 20, 30)
    val createdAt = cal.getTime()
    val mlProposals = for (i <- 21 to 40) yield MlProposal(
      i,
      "申請者の名前",
      "proposer@example.com",
      "MLタイトル" + i,
      MlProposalStatus.New,
      Some(MlArchiveType.Mailman),
      Some(new URL("http://localhost/path/to/archive/")),
      Some("コメント(MLの説明など)"),
      createdAt,
      createdAt
    )
    
    m expects 'search withArgs(
      Filter(MLPFilterBy.Status, "new"),
      Page(2, 20),
      Sort(MLPSortBy.MlTitle, SortOrder.Ascending)
    ) returning MlProposalSearchResult(100, 21, 20, mlProposals.toList)

    val response = ComponentRegistry.mlProposalService.doWith(m) {
      new MlProposalResource().list("status", "new", "mlTitle", null, "2", "20")
    }
    expect(200) { response.getStatus() }

    val json = """{
      |"totalResults":100,
      |"startIndex":21,
      |"itemsPerPage":20,
      |"mlProposals":[{
        |"id":21,
        |"proposerName":"申請者の名前",
        |"proposerEmail":"proposer@example.com",
        |"mlTitle":"MLタイトル21",
        |"status":"new",
        |"archiveType":"mailman",
        |"archiveUrl":"http://localhost/path/to/archive/",
        |"comment":"コメント(MLの説明など)",
        |"createdAt":"2012-10-28T10:20:30",
        |"updatedAt":"2012-10-28T10:20:30"
      |}""".stripMargin.replaceAll("\n", "")
      
    expect(json) {
      response.getEntity.toString.substring(0, json.length)
    }
  }  

  test("get when sortOrder is invalid") {
    val response = new MlProposalResource().list("status", "new", "mlTitle", "invalid", "1", "10")
    expect(400) { response.getStatus() }
  }
  
  test("get when startPage is 0") {
    val response = new MlProposalResource().list("status", "new", "mlTitle", "ascending", "0", "20")
    expect(400) { response.getStatus() }
  }
  
  test("get when startPage is -1") {
    val response = new MlProposalResource().list("status", "new", "mlTitle", "ascending", "-1", "20")
    expect(400) { response.getStatus() }
  }
  
  test("get when count is 0") {
    val response = new MlProposalResource().list("status", "new", "mlTitle", "ascending", "1", "0")
    expect(400) { response.getStatus() }
  }
  
  test("get when count is -1") {
    val response = new MlProposalResource().list("status", "new", "mlTitle", "ascending", "1", "-1")
    expect(400) { response.getStatus() }
  }
  
  test("get when count is 101") {
    val response = new MlProposalResource().list("status", "new", "mlTitle", "ascending", "1", "101")
    expect(400) { response.getStatus() }
  }
  
  test("get any items by no filter") {
    val m = mock[MlProposalService]
    
    m expects 'search withArgs(
      Page(1, 10),
      Sort(MLPSortBy.MlTitle, SortOrder.Ascending)
    ) returning MlProposalSearchResult(0, 1, 10, Nil)

    val response = ComponentRegistry.mlProposalService.doWith(m) {
      new MlProposalResource().list(null, null, "mlTitle", "ascending", "1", "10")
    }
    expect(200) { response.getStatus() }
    
    val json = """{
      |"totalResults":0,
      |"startIndex":1,
      |"itemsPerPage":10,
      |"mlProposals":[]
      |}""".stripMargin.replaceAll("\n", "")
    expect(json) { response.getEntity() }
  }

  test("get any items by filter") {
    val m = mock[MlProposalService]
    
    m expects 'search withArgs(
      Filter(MLPFilterBy.Status, "new"),
      Page(1, 10),
      Sort(MLPSortBy.MlTitle, SortOrder.Ascending)
    ) returning MlProposalSearchResult(0, 1, 10, Nil)

    val response = ComponentRegistry.mlProposalService.doWith(m) {
      new MlProposalResource().list("status", "new", "mlTitle", "ascending", "1", "10")
    }
    expect(200) { response.getStatus() }
    
    val json = """{
      |"totalResults":0,
      |"startIndex":1,
      |"itemsPerPage":10,
      |"mlProposals":[]
      |}""".stripMargin.replaceAll("\n", "")
    expect(json) { response.getEntity() }
  }

}