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
import org.milmsearch.core.test.util.DateUtil
import org.scalamock.Mock
import org.milmsearch.core.test.util.MockCreatable
import org.milmsearch.core.domain.CreateMlProposalRequest
import org.scalatest.PrivateMethodTester

class MlProposalResourceSuite extends FunSuite
    with MockFactory with ProxyMockFactory with MockCreatable
    with PrivateMethodTester {

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
      Some("コメント(MLの説明など)"))

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
  
  test("createFilter 絞り込み項目と値を指定した場合") {
    val filter = new MlProposalResource invokePrivate
      PrivateMethod[Option[Filter[MLPFilterBy.type]]](
        'createFilter)(Some("status"), Some("new"))

    expect(true)(filter isDefined)
    expect(MLPFilterBy.Status)(filter.get.column)
    expect("new")(filter.get.value)
  }

  test("createFilter 絞り込み項目を指定して、絞り込み値を指定しなかった場合") {
    val e = intercept[BadQueryParameterException] {
      new MlProposalResource invokePrivate
        PrivateMethod[Option[Filter[MLPFilterBy.type]]](
          'createFilter)(Some("status"), None)
    }

    expect(true)(e.getMessage().startsWith("Invalid filter."))
  }

  test("createFilter 絞り込み項目を指定しないで、絞り込み値を指定した場合") {
    val e = intercept[BadQueryParameterException] {
      new MlProposalResource invokePrivate
        PrivateMethod[Option[Filter[MLPFilterBy.type]]](
          'createFilter)(None, Some("new"))
    }

    expect(true)(e.getMessage().startsWith("Invalid filter."))
  }

  test("createFilter 絞り込み項目が規定外の場合") {
    val e = intercept[BadQueryParameterException] {
      new MlProposalResource invokePrivate
        PrivateMethod[Option[Filter[MLPFilterBy.type]]](
          'createFilter)(Some("hello"), Some("new"))
    }

    expect("Can't create filter. by[hello], value[new]")(
      e.getMessage())
  }

  test("createFilter 絞り込み項目と絞り込み値を指定しなかった場合") {
    val filter = new MlProposalResource invokePrivate
      PrivateMethod[Option[Filter[MLPFilterBy.type]]](
        'createFilter)(None, None)

    expect(None)(filter)
  }

  test("createPage ページに 1、カウントに 1 を指定した場合") {
    val page = new MlProposalResource invokePrivate
      PrivateMethod[Page]('createPage)(1L, 1L)

    expect(1L)(page.page)
    expect(1L)(page.count)
  }

  test("createPage ページに 0 を指定した場合") {
    val e = intercept[BadQueryParameterException] {
      new MlProposalResource invokePrivate
        PrivateMethod[Page]('createPage)(0L, 1L)
    }

    expect("Invalid startPage value. [0]")(
      e.getMessage())
  }

  test("createPage カウントに 0 を指定した場合") {
    val e = intercept[BadQueryParameterException] {
      new MlProposalResource invokePrivate
        PrivateMethod[Page]('createPage)(1L, 0L)
    }

    expect("Invalid count value. [0]")(
      e.getMessage())
  }

  test("createPage カウントに 100 を指定した場合") {
    val page = new MlProposalResource invokePrivate
        PrivateMethod[Page]('createPage)(1L, 100L)

    expect(1L)(page.page)
    expect(100L)(page.count)
  }

  test("createPage カウントに 101 を指定した場合") {
    val e = intercept[BadQueryParameterException] {
      new MlProposalResource invokePrivate
        PrivateMethod[Page]('createPage)(1L, 101L)
    }

    expect("Invalid count value. [101]")(
      e.getMessage())
  }

  test("createSort ソート列名と値を指定した場合") {
    val sort = new MlProposalResource invokePrivate
      PrivateMethod[Option[Sort[MLPSortBy.type]]](
        'createSort)(Some("createdAt"), Some("ascending"))

    expect(true)(sort isDefined)
    expect(MLPSortBy.CreatedAt)(sort.get.column)
    expect(SortOrder.Ascending)(sort.get.sortOrder)
  }

  test("createSort ソート列名を指定して、ソート順序を指定しなかった場合") {
    val e = intercept[BadQueryParameterException] {
      new MlProposalResource invokePrivate
        PrivateMethod[Option[Sort[MLPSortBy.type]]](
          'createSort)(Some("createdAt"), None)
    }

    expect(true)(e.getMessage().startsWith("Invalid sort."))
  }

  test("createSort ソート列名を指定しないで、ソート順序を指定した場合") {
    val e = intercept[BadQueryParameterException] {
      new MlProposalResource invokePrivate
        PrivateMethod[Option[Sort[MLPSortBy.type]]](
          'createSort)(None, Some("ascending"))
    }

    expect(true)(e.getMessage().startsWith("Invalid sort."))
  }

  test("createSort ソート列名が規定外の場合") {
    val e = intercept[BadQueryParameterException] {
      new MlProposalResource invokePrivate
        PrivateMethod[Option[Sort[MLPSortBy.type]]](
          'createSort)(Some("hello"), Some("ascending"))
    }

    expect("Can't create sort. by[hello], order[ascending]")(
      e.getMessage())
  }

  test("createSort ソート列名とソート順序を指定しなかった場合") {
    val sort = new MlProposalResource invokePrivate
      PrivateMethod[Option[Sort[MLPSortBy.type]]](
        'createSort)(None, None)

    expect(None)(sort)
  }

  test("list パラメータがすべて正常値の場合") {
    val response = ComponentRegistry.mlProposalService.doWith(
      createMock[MlProposalService] {
        _ expects 'search withArgs (
            Page(2, 20),
            Some(Sort(MLPSortBy.ArchiveType, SortOrder.Ascending)),
            Some(Filter(MLPFilterBy.Status, "new"))
          ) returning MlProposalSearchResult(100, 21, 20,
              21 to 40 map { i => MlProposal(
                i,
                "申請者の名前",
                "proposer@example.com",
                "MLタイトル" + i,
                MlProposalStatus.New,
                Some(MlArchiveType.Mailman),
                Some(new URL("http://localhost/path/to/archive/")),
                Some("コメント(MLの説明など)"),
                DateUtil.createDate("2012/10/28 10:20:30"),
                DateUtil.createDate("2012/10/28 10:20:30"))
              } toList)
      }) {
        new MlProposalResource().list(
          filterBy    = "status",
          filterValue = "new",
          startPage   = "2",
          count       = "20",
          sortBy      = "archiveType",
          sortOrder   = "ascending")
      }

    expect(200) { response.getStatus() }
    expect(
      """{
      |"totalResults":100,
      |"startIndex":21,
      |"itemsPerPage":20,
      |"mlProposals":[%s]
      |}""".stripMargin format (21 to 40 map { i =>
        """{
        |"id":%s,
        |"proposerName":"申請者の名前",
        |"proposerEmail":"proposer@example.com",
        |"mlTitle":"MLタイトル%s",
        |"status":"new",
        |"archiveType":"mailman",
        |"archiveUrl":"http://localhost/path/to/archive/",
        |"comment":"コメント(MLの説明など)",
        |"createdAt":"2012-10-28T10:20:30+09:00",
        |"updatedAt":"2012-10-28T10:20:30+09:00"
        |}""".stripMargin format (i, i)
      } mkString ",") replaceAll ("\n", "")
    ) { response.getEntity.toString }
  }

  test("list パラメータが全て null の場合") {
    val response = ComponentRegistry.mlProposalService.doWith(
      createMock[MlProposalService] {
        _ expects 'search withArgs (
            Page(1, 10), None, None
          ) returning MlProposalSearchResult(100, 1, 10,
              1 to 10 map { i => MlProposal(
                i,
                "申請者の名前",
                "proposer@example.com",
                "MLタイトル" + i,
                MlProposalStatus.New,
                Some(MlArchiveType.Mailman),
                Some(new URL("http://localhost/path/to/archive/")),
                Some("コメント(MLの説明など)"),
                DateUtil.createDate("2012/10/28 10:20:30"),
                DateUtil.createDate("2012/10/28 10:20:30"))
              } toList)
      }) {
        new MlProposalResource().list(
          filterBy    = null,
          filterValue = null,
          startPage   = null,
          count       = null,
          sortBy      = null,
          sortOrder   = null)
      }

    expect(200) { response.getStatus() }
    expect(
      """{
      |"totalResults":100,
      |"startIndex":1,
      |"itemsPerPage":10,
      |"mlProposals":[%s]
      |}""".stripMargin format (1 to 10 map { i =>
        """{
        |"id":%s,
        |"proposerName":"申請者の名前",
        |"proposerEmail":"proposer@example.com",
        |"mlTitle":"MLタイトル%s",
        |"status":"new",
        |"archiveType":"mailman",
        |"archiveUrl":"http://localhost/path/to/archive/",
        |"comment":"コメント(MLの説明など)",
        |"createdAt":"2012-10-28T10:20:30+09:00",
        |"updatedAt":"2012-10-28T10:20:30+09:00"
        |}""".stripMargin format (i, i)
      } mkString ",") replaceAll ("\n", "")
    ) { response.getEntity.toString }
  }

  test("list 絞り込み項目が null で且つ絞り込み値が指定された場合") {
    val response = new MlProposalResource().list(
      filterBy    = null,
      filterValue = "new",
      startPage   = "2",
      count       = "20",
      sortBy      = "mlTitle",
      sortOrder   = "ascending")

    expect(400) { response.getStatus() }
  }

  test("list 絞り込み項目が存在しない項目名の場合") {
    val response = new MlProposalResource().list(
      filterBy    = "hello",
      filterValue = "new",
      startPage   = "2",
      count       = "20",
      sortBy      = "mlTitle",
      sortOrder   = "ascending")

    expect(400) { response.getStatus() }
  }

  test("list 絞り込み値が null の場合") {
    val response = new MlProposalResource().list(
      filterBy    = "status",
      filterValue = null,
      startPage   = "2",
      count       = "20",
      sortBy      = "mlTitle",
      sortOrder   = "ascending")

    expect(400) { response.getStatus() }
  }

  test("list ソート列名が null の場合") {
    val response = new MlProposalResource().list(
      filterBy    = null,
      filterValue = null,
      startPage   = "2",
      count       = "20",
      sortBy      = null,
      sortOrder   = "ascending")

    expect(400) { response.getStatus() }
  }

  test("list ソート列名が存在しない項目名の場合") {
    val response = new MlProposalResource().list(
      filterBy    = "status",
      filterValue = "new",
      startPage   = "2",
      count       = "20",
      sortBy      = "hello",
      sortOrder   = "ascending")

    expect(400) { response.getStatus() }
  }

  test("list 並び順が null の場合") {
    val response = new MlProposalResource().list(
      filterBy    = "status",
      filterValue = "new",
      startPage   = "2",
      count       = "20",
      sortBy      = "mlTitle",
      sortOrder   = null)

    expect(400) { response.getStatus() }
  }

  test("list 並び順が規定外の場合") {
    val response = new MlProposalResource().list(
      filterBy    = "status",
      filterValue = "new",
      startPage   = "1",
      count       = "10",
      sortBy      = "mlTitle",
      sortOrder   = "invalid")

    expect(400) { response.getStatus() }
  }

  test("list ページ番号に 0 を指定した場合") {
    val response = new MlProposalResource().list(
      filterBy    = "status",
      filterValue = "new",
      startPage   = "0",
      count       = "20",
      sortBy      = "mlTitle",
      sortOrder   = "ascending")

    expect(400) { response.getStatus() }
  }

  test("list ページ番号に -1 を指定した場合") {
    val response = new MlProposalResource().list(
      filterBy    = "status",
      filterValue = "new",
      startPage   = "-1",
      count       = "20",
      sortBy      = "mlTitle",
      sortOrder   = "ascending")

    expect(400) { response.getStatus() }
  }

  test("list ページ番号に 'a' を指定した場合") {
    val response = new MlProposalResource().list(
      filterBy    = "status",
      filterValue = "new",
      startPage   = "a",
      count       = "20",
      sortBy      = "mlTitle",
      sortOrder   = "ascending")

    expect(400) { response.getStatus() }
  }

  test("list 項目数に 0 を指定した場合") {
    val response = new MlProposalResource().list(
      filterBy    = "status",
      filterValue = "new",
      startPage   = "1",
      count       = "0",
      sortBy      = "mlTitle",
      sortOrder   = "ascending")

    expect(400) { response.getStatus() }
  }

  test("list 項目数に -1 を指定した場合") {
    val response = new MlProposalResource().list(
      filterBy    = "status",
      filterValue = "new",
      startPage   = "1",
      count       = "-1",
      sortBy      = "mlTitle",
      sortOrder   = "ascending")

    expect(400) { response.getStatus() }
  }

  test("list 項目数に 'a' を指定した場合") {
    val response = new MlProposalResource().list(
      filterBy    = "status",
      filterValue = "new",
      startPage   = "1",
      count       = "a",
      sortBy      = "mlTitle",
      sortOrder   = "ascending")

    expect(400) { response.getStatus() }
  }

  test("list 項目数に 101 を指定した場合") {
    val response = new MlProposalResource().list(
      filterBy    = "status",
      filterValue = "new",
      startPage   = "1",
      count       = "101",
      sortBy      = "mlTitle",
      sortOrder   = "ascending")

    expect(400) { response.getStatus() }
  }

  test("list 絞り込みを指定せずに取得結果 0 件の場合") {
    val response = ComponentRegistry.mlProposalService.doWith(
      createMock[MlProposalService] {
        _ expects 'search withArgs (
            Page(1, 10),
            Some(Sort(MLPSortBy.MlTitle, SortOrder.Ascending)),
            None
          ) returning MlProposalSearchResult(0, 1, 10, Nil)
      }) {
        new MlProposalResource().list(
          filterBy    = null,
          filterValue = null,
          startPage   = "1",
          count       = "10",
          sortBy      = "mlTitle",
          sortOrder   = "ascending")
      }

    expect(200) { response.getStatus() }
    expect(
      """{
      |"totalResults":0,
      |"startIndex":1,
      |"itemsPerPage":10,
      |"mlProposals":[]
      |}""".stripMargin replaceAll ("\n", "")
    ) { response.getEntity.toString }
  }

  test("list 絞り込みを指定して取得結果 0 件の場合") {
    val response = ComponentRegistry.mlProposalService.doWith(
      createMock[MlProposalService] {
        _ expects 'search withArgs (
          Page(1, 10),
          Some(Sort(MLPSortBy.MlTitle, SortOrder.Ascending)),
          Some(Filter(MLPFilterBy.Status, "new"))
        ) returning MlProposalSearchResult(0, 1, 10, Nil)
      }) {
        new MlProposalResource().list(
          filterBy    = "status",
          filterValue = "new",
          startPage   = "1",
          count       = "10",
          sortBy      = "mlTitle",
          sortOrder   = "ascending")
      }

    expect(200) { response.getStatus() }
    expect(
      """{
      |"totalResults":0,
      |"startIndex":1,
      |"itemsPerPage":10,
      |"mlProposals":[]
      |}""".stripMargin replaceAll ("\n", "")
    ) { response.getEntity.toString }
  }
  test("delete_正常") {
    // mockは戻り値なしで良い。
    val id = "1"

    val m = mock[MlProposalService]
    m expects 'delete withArgs (1L) returning true

    val response = ComponentRegistry.mlProposalService.doWith(m) {
      new MlProposalResource().delete(id)
    }

    expect(204) {
      response.getStatus
    }
  }

  test("delete_id該当なし") {
    // mockは戻り値なしで良い。
    val id = "1"

    val m = mock[MlProposalService]
    m expects 'delete withArgs (1L) returning false

    val response = ComponentRegistry.mlProposalService.doWith(m) {
      new MlProposalResource().delete(id)
    }

    expect(404) {
      response.getStatus
    }
  }

  test("delete_id数値エラー") {
    // mockは戻り値なしで良い。
    val id = "a"

    val m = mock[MlProposalService]
    //m expects 'delete withArgs (1L) returning true

    val response = ComponentRegistry.mlProposalService.doWith(m) {
      new MlProposalResource().delete(id)
    }

    expect(400) {
      response.getStatus
    }
  }
  
    test("delete_サーバエラー") {
    // mockが例外を発生させる
    val id = "1"

    val m = mock[MlProposalService]
    m expects 'delete withArgs (1L) throws new RuntimeException("Server Error!")

    val response = ComponentRegistry.mlProposalService.doWith(m) {
      new MlProposalResource().delete(id)
    }

    expect(500) {
      response.getStatus
    }
  }

}