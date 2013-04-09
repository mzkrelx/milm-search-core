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
import org.milmsearch.core.domain.CreateMLProposalRequest
import org.milmsearch.core.domain.MLArchiveType
import org.milmsearch.core.domain.MLProposal
import org.milmsearch.core.domain.MLProposalStatus
import org.milmsearch.core.service.MLProposalService
import org.scalamock.scalatest.MockFactory
import org.scalamock.ProxyMockFactory
import org.scalatest.FunSuite
import org.milmsearch.core.domain.Filter
import org.milmsearch.core.domain.Sort
import org.milmsearch.core.domain.SortOrder
import org.milmsearch.core.domain.Page
import org.milmsearch.core.domain.MLProposal
import java.util.Date
import java.util.Calendar
import java.text.SimpleDateFormat
import org.milmsearch.core.domain.MLProposalSearchResult
import org.apache.commons.lang3.time.DateUtils
import org.milmsearch.core.domain.{MLProposalFilterBy => MLPFilterBy}
import org.milmsearch.core.domain.{MLProposalSortBy => MLPSortBy}
import org.milmsearch.core.test.util.DateUtil
import org.scalamock.Mock
import org.milmsearch.core.test.util.MockCreatable
import org.milmsearch.core.domain.CreateMLProposalRequest
import org.scalatest.PrivateMethodTester
import org.milmsearch.core.exception.ResourceNotFoundException
import org.milmsearch.core.ComponentRegistry
import java.util.Date
import org.milmsearch.core.domain.UpdateMLProposalRequest

class MLProposalResourceSuite extends FunSuite
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
      |  "archiveURL": "http://localhost/path/to/archive/",
      |  "comment": "コメント(MLの説明など)"
      |}""".stripMargin

    val request = CreateMLProposalRequest(
      "申請者の名前",
      "proposer@example.com",
      "MLタイトル",
      MLProposalStatus.New,
      Some(MLArchiveType.Mailman),
      Some(new URL("http://localhost/path/to/archive/")),
      Some("コメント(MLの説明など)"))

    val m = mock[MLProposalService]
    m expects 'create withArgs(request) returning 1L

    val response = ComponentRegistry.mlProposalService.doWith(m) {
      new MLProposalResource().create(json)
    }

    expect(201) { response.getStatus }
    expect(new URI("/ml-proposals/1")) {
      response.getMetadata().getFirst("Location")
    }
  }

  test("createFilter 絞り込み項目と値を指定した場合") {
    val filter = new MLProposalResource invokePrivate
      PrivateMethod[Option[Filter[MLPFilterBy.type]]](
        'createFilter)(Some("status"), Some("new"))

    expect(true)(filter isDefined)
    expect(MLPFilterBy.Status)(filter.get.column)
    expect(MLProposalStatus.New)(filter.get.value)
  }

  test("createFilter 絞り込み項目を指定して、絞り込み値を指定しなかった場合") {
    val e = intercept[BadQueryParameterException] {
      new MLProposalResource invokePrivate
        PrivateMethod[Option[Filter[MLPFilterBy.type]]](
          'createFilter)(Some("status"), None)
    }

    expect(true)(e.getMessage().startsWith("Invalid filter."))
  }

  test("createFilter 絞り込み項目を指定しないで、絞り込み値を指定した場合") {
    val e = intercept[BadQueryParameterException] {
      new MLProposalResource invokePrivate
        PrivateMethod[Option[Filter[MLPFilterBy.type]]](
          'createFilter)(None, Some("new"))
    }

    expect(true)(e.getMessage().startsWith("Invalid filter."))
  }

  test("createFilter 絞り込み項目が規定外の場合") {
    val e = intercept[BadQueryParameterException] {
      new MLProposalResource invokePrivate
        PrivateMethod[Option[Filter[MLPFilterBy.type]]](
          'createFilter)(Some("hello"), Some("new"))
    }

    expect("Can't create filter. by[hello]")(
      e.getMessage())
  }

  test("createFilter 絞り込み項目と絞り込み値を指定しなかった場合") {
    val filter = new MLProposalResource invokePrivate
      PrivateMethod[Option[Filter[MLPFilterBy.type]]](
        'createFilter)(None, None)

    expect(None)(filter)
  }

  test("createPage ページに 1、カウントに 1 を指定した場合") {
    val page = new MLProposalResource().createPage(1L, 1L)

    expect(1L)(page.page)
    expect(1L)(page.count)
  }

  test("createPage ページに 0 を指定した場合") {
    val e = intercept[BadQueryParameterException] {
      new MLProposalResource().createPage(0L, 1L)
    }

    expect("Invalid startPage value. [0]")(
      e.getMessage())
  }

  test("createPage カウントに 0 を指定した場合") {
    val e = intercept[BadQueryParameterException] {
      new MLProposalResource().createPage(1L, 0L)
    }

    expect("Invalid count value. [0]")(
      e.getMessage())
  }

  test("createPage カウントに 100 を指定した場合") {
    val page = new MLProposalResource().createPage(1L, 100L)

    expect(1L)(page.page)
    expect(100L)(page.count)
  }

  test("createPage カウントに 101 を指定した場合") {
    val e = intercept[BadQueryParameterException] {
      new MLProposalResource().createPage(1L, 101L)
    }

    expect("Invalid count value. [101]")(
      e.getMessage())
  }

  test("list パラメータがすべて正常値の場合") {
    val response = ComponentRegistry.mlProposalService.doWith(
      createMock[MLProposalService] {
        _ expects 'search withArgs (
            Page(2, 20),
            Some(Sort(MLPSortBy.ArchiveType, SortOrder.Ascending)),
            Some(Filter(MLPFilterBy.Status, MLProposalStatus.New))
          ) returning MLProposalSearchResult(100, 21, 20,
              21 to 40 map { i => MLProposal(
                i,
                "申請者の名前",
                "proposer@example.com",
                "MLタイトル" + i,
                MLProposalStatus.New,
                Some(MLArchiveType.Mailman),
                Some(new URL("http://localhost/path/to/archive/")),
                Some("コメント(MLの説明など)"),
                DateUtil.createDate("2012/10/28 10:20:30"),
                DateUtil.createDate("2012/10/28 10:20:30"),
                None,
                Some("管理者コメント"))
              } toList)
      }) {
        new MLProposalResource().list(
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
      |"items":[%s]
      |}""".stripMargin format (21 to 40 map { i =>
        """{
        |"id":%s,
        |"proposerName":"申請者の名前",
        |"proposerEmail":"proposer@example.com",
        |"mlTitle":"MLタイトル%s",
        |"status":"new",
        |"archiveType":"mailman",
        |"archiveURL":"http://localhost/path/to/archive/",
        |"comment":"コメント(MLの説明など)",
        |"createdAt":"2012-10-28T10:20:30+09:00",
        |"updatedAt":"2012-10-28T10:20:30+09:00",
        |"judgedAt":""
        |}""".stripMargin format (i, i)
      } mkString ",") replaceAll ("\n", "")
    ) { response.getEntity.toString }
  }

  test("list パラメータが全て null の場合") {
    val response = ComponentRegistry.mlProposalService.doWith(
      createMock[MLProposalService] {
        _ expects 'search withArgs (
            Page(1, 10), None, None
          ) returning MLProposalSearchResult(100, 1, 10,
              1 to 10 map { i => MLProposal(
                i,
                "申請者の名前",
                "proposer@example.com",
                "MLタイトル" + i,
                MLProposalStatus.New,
                Some(MLArchiveType.Mailman),
                Some(new URL("http://localhost/path/to/archive/")),
                Some("コメント(MLの説明など)"),
                DateUtil.createDate("2012/10/28 10:20:30"),
                DateUtil.createDate("2012/10/28 10:20:30"),
                None,
                Some("管理者コメント"))
              } toList)
      }) {
        new MLProposalResource().list(
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
      |"items":[%s]
      |}""".stripMargin format (1 to 10 map { i =>
        """{
        |"id":%s,
        |"proposerName":"申請者の名前",
        |"proposerEmail":"proposer@example.com",
        |"mlTitle":"MLタイトル%s",
        |"status":"new",
        |"archiveType":"mailman",
        |"archiveURL":"http://localhost/path/to/archive/",
        |"comment":"コメント(MLの説明など)",
        |"createdAt":"2012-10-28T10:20:30+09:00",
        |"updatedAt":"2012-10-28T10:20:30+09:00",
        |"judgedAt":""
        |}""".stripMargin format (i, i)
      } mkString ",") replaceAll ("\n", "")
    ) { response.getEntity.toString }
  }

  test("list 絞り込み項目が null で且つ絞り込み値が指定された場合") {
    val response = new MLProposalResource().list(
      filterBy    = null,
      filterValue = "new",
      startPage   = "2",
      count       = "20",
      sortBy      = "mlTitle",
      sortOrder   = "ascending")

    expect(400) { response.getStatus() }
  }

  test("list 絞り込み項目が存在しない項目名の場合") {
    val response = new MLProposalResource().list(
      filterBy    = "hello",
      filterValue = "new",
      startPage   = "2",
      count       = "20",
      sortBy      = "mlTitle",
      sortOrder   = "ascending")

    expect(400) { response.getStatus() }
  }

  test("list 絞り込み値が null の場合") {
    val response = new MLProposalResource().list(
      filterBy    = "status",
      filterValue = null,
      startPage   = "2",
      count       = "20",
      sortBy      = "mlTitle",
      sortOrder   = "ascending")

    expect(400) { response.getStatus() }
  }

  test("list ソート列名が null の場合") {
    val response = new MLProposalResource().list(
      filterBy    = null,
      filterValue = null,
      startPage   = "2",
      count       = "20",
      sortBy      = null,
      sortOrder   = "ascending")

    expect(400) { response.getStatus() }
  }

  test("list ソート列名が存在しない項目名の場合") {
    val response = new MLProposalResource().list(
      filterBy    = "status",
      filterValue = "new",
      startPage   = "2",
      count       = "20",
      sortBy      = "hello",
      sortOrder   = "ascending")

    expect(400) { response.getStatus() }
  }

  test("list 並び順が null の場合") {
    val response = new MLProposalResource().list(
      filterBy    = "status",
      filterValue = "new",
      startPage   = "2",
      count       = "20",
      sortBy      = "mlTitle",
      sortOrder   = null)

    expect(400) { response.getStatus() }
  }

  test("list 並び順が規定外の場合") {
    val response = new MLProposalResource().list(
      filterBy    = "status",
      filterValue = "new",
      startPage   = "1",
      count       = "10",
      sortBy      = "mlTitle",
      sortOrder   = "invalid")

    expect(400) { response.getStatus() }
  }

  test("list ページ番号に 0 を指定した場合") {
    val response = new MLProposalResource().list(
      filterBy    = "status",
      filterValue = "new",
      startPage   = "0",
      count       = "20",
      sortBy      = "mlTitle",
      sortOrder   = "ascending")

    expect(400) { response.getStatus() }
  }

  test("list ページ番号に -1 を指定した場合") {
    val response = new MLProposalResource().list(
      filterBy    = "status",
      filterValue = "new",
      startPage   = "-1",
      count       = "20",
      sortBy      = "mlTitle",
      sortOrder   = "ascending")

    expect(400) { response.getStatus() }
  }

  test("list ページ番号に 'a' を指定した場合") {
    val response = new MLProposalResource().list(
      filterBy    = "status",
      filterValue = "new",
      startPage   = "a",
      count       = "20",
      sortBy      = "mlTitle",
      sortOrder   = "ascending")

    expect(400) { response.getStatus() }
  }

  test("list 項目数に 0 を指定した場合") {
    val response = new MLProposalResource().list(
      filterBy    = "status",
      filterValue = "new",
      startPage   = "1",
      count       = "0",
      sortBy      = "mlTitle",
      sortOrder   = "ascending")

    expect(400) { response.getStatus() }
  }

  test("list 項目数に -1 を指定した場合") {
    val response = new MLProposalResource().list(
      filterBy    = "status",
      filterValue = "new",
      startPage   = "1",
      count       = "-1",
      sortBy      = "mlTitle",
      sortOrder   = "ascending")

    expect(400) { response.getStatus() }
  }

  test("list 項目数に 'a' を指定した場合") {
    val response = new MLProposalResource().list(
      filterBy    = "status",
      filterValue = "new",
      startPage   = "1",
      count       = "a",
      sortBy      = "mlTitle",
      sortOrder   = "ascending")

    expect(400) { response.getStatus() }
  }

  test("list 項目数に 101 を指定した場合") {
    val response = new MLProposalResource().list(
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
      createMock[MLProposalService] {
        _ expects 'search withArgs (
            Page(1, 10),
            Some(Sort(MLPSortBy.MLTitle, SortOrder.Ascending)),
            None
          ) returning MLProposalSearchResult(0, 1, 10, Nil)
      }) {
        new MLProposalResource().list(
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
      |"items":[]
      |}""".stripMargin replaceAll ("\n", "")
    ) { response.getEntity.toString }
  }

  test("list 絞り込みを指定して取得結果 0 件の場合") {
    val response = ComponentRegistry.mlProposalService.doWith(
      createMock[MLProposalService] {
        _ expects 'search withArgs (
          Page(1, 10),
          Some(Sort(MLPSortBy.MLTitle, SortOrder.Ascending)),
          Some(Filter(MLPFilterBy.Status, MLProposalStatus.New))
        ) returning MLProposalSearchResult(0, 1, 10, Nil)
      }) {
        new MLProposalResource().list(
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
      |"items":[]
      |}""".stripMargin replaceAll ("\n", "")
    ) { response.getEntity.toString }
  }

  test("delete_正常") {
    val id = "1"

    val m = mock[MLProposalService]
    m expects 'delete withArgs (1L) returning true

    val response = ComponentRegistry.mlProposalService.doWith(m) {
      new MLProposalResource().delete(id)
    }

    expect(204) {
      response.getStatus
    }
  }

  test("delete_id該当なし") {
    val id = "1"

    val m = mock[MLProposalService]
    m expects 'delete withArgs (1L) throws new ResourceNotFoundException("Not found.")

    val response = ComponentRegistry.mlProposalService.doWith(m) {
      new MLProposalResource().delete(id)
    }

    expect(404) {
      response.getStatus
    }
  }

  test("delete_id数値エラー") {
    val id = "a"
    val response = new MLProposalResource().delete(id)

    expect(400) {
      response.getStatus
    }
  }

  test("delete_id Nullエラー") {
    val id = null
    val response =  new MLProposalResource().delete(id)

    expect(400) {
      response.getStatus
    }
  }

  test("update") {
    val json = """
      |{
      |  "mlTitle": "MLタイトル",
      |  "archiveType": "mailman",
      |  "archiveURL": "http://localhost/path/to/archive/",
      |  "adminComment": "管理者コメント"
      |}""".stripMargin

    val m = mock[MLProposalService]
    m expects 'update withArgs(1,
      UpdateMLProposalRequest("MLタイトル",
        MLArchiveType.Mailman,
        new URL("http://localhost/path/to/archive/"),
        Some("管理者コメント")))

    val response = ComponentRegistry.mlProposalService.doWith(m) {
      new MLProposalResource().update("1", json)
    }

    expect(204) { response.getStatus }
  }

  test("update notfound") {
    val json = """
      |{
      |  "mlTitle": "MLタイトル",
      |  "archiveType": "mailman",
      |  "archiveURL": "http://localhost/path/to/archive/",
      |  "adminComment": "管理者コメント"
      |}""".stripMargin

    val m = mock[MLProposalService]
    m expects 'update withArgs(1,
      UpdateMLProposalRequest("MLタイトル",
        MLArchiveType.Mailman,
        new URL("http://localhost/path/to/archive/"),
        Some("管理者コメント"))
    ) throws new ResourceNotFoundException("any")

    val response = ComponentRegistry.mlProposalService.doWith(m) {
      new MLProposalResource().update("1", json)
    }

    expect(404) { response.getStatus }
  }

  test("update id illegal format") {
    val json = """
      |{
      |  "mlTitle": "MLタイトル",
      |  "archiveType": "mailman",
      |  "archiveURL": "http://localhost/path/to/archive/",
      |}""".stripMargin

    val response = new MLProposalResource().update("one", json)

    expect(400) { response.getStatus }
  }

  test("update json の項目が多い場合はエラーにならない") {
    val json = """
      |{
      |  "proposerName": "申請者の名前",
      |  "proposerEmail": "proposer@example.com",
      |  "mlTitle": "MLタイトル",
      |  "status": "new",
      |  "archiveType": "mailman",
      |  "archiveURL": "http://localhost/path/to/archive/",
      |  "comment": "コメント(MLの説明など)"
      |  "adminComment": "管理者コメント"
      |}""".stripMargin

    val m = mock[MLProposalService]
    m expects 'update withArgs(1,
      UpdateMLProposalRequest("MLタイトル",
        MLArchiveType.Mailman,
        new URL("http://localhost/path/to/archive/"),
        Some("管理者コメント")))

    val response = ComponentRegistry.mlProposalService.doWith(m) {
      new MLProposalResource().update("1", json)
    }

    expect(204) { response.getStatus }
  }

  test("update json の項目が足りない場合はエラー") {
    val json = """
      |{
      |  "archiveType": "mailman",
      |}""".stripMargin

    val response = new MLProposalResource().update("1", json)

    expect(400) { response.getStatus }
  }

  test("show パラメータがすべて正常値の場合") {
    val response = ComponentRegistry.mlProposalService.doWith(
      createMock[MLProposalService] {
        _ expects 'find withArgs (1) returning
          Some(MLProposal(
            1,
            "申請者の名前",
            "proposer@example.com",
            "MLタイトル",
            MLProposalStatus.New,
            Some(MLArchiveType.Mailman),
            Some(new URL("http://localhost/path/to/archive/")),
            Some("コメント(MLの説明など)"),
            DateUtil.createDate("2012/10/28 10:20:30"),
            DateUtil.createDate("2012/10/28 10:20:30"),
            None,
            Some("管理者コメント")))
      })(new MLProposalResource().show("1"))

    expect(200) { response.getStatus() }
    expect(
      """{
        |"id":%s,
        |"proposerName":"申請者の名前",
        |"proposerEmail":"proposer@example.com",
        |"mlTitle":"MLタイトル",
        |"status":"new",
        |"archiveType":"mailman",
        |"archiveURL":"http://localhost/path/to/archive/",
        |"comment":"コメント(MLの説明など)",
        |"createdAt":"2012-10-28T10:20:30+09:00",
        |"updatedAt":"2012-10-28T10:20:30+09:00",
        |"judgedAt":""
        |}""".stripMargin format (1) replaceAll ("\n", "")
    ) { response.getEntity.toString }
  }

  test("show パラメータが数値でない場合") {
    val response = new MLProposalResource().show("a")
    expect(400) { response.getStatus() }
  }

  test("show パラメータが null の場合") {
    val response = new MLProposalResource().show(null)
    expect(400) { response.getStatus() }
  }

  test("accept 承認する場合") {
    val response = ComponentRegistry.mlProposalService.doWith{
      createMock[MLProposalService] {
        _ expects 'accept withArgs(1)
      }
    } { new MLProposalResource().accept("1", "true") }
    expect(204) { response.getStatus() }  // TODO
  }

  test("accept 却下する場合") {
    val response = ComponentRegistry.mlProposalService.doWith{
      createMock[MLProposalService] {
        _ expects 'reject withArgs(1)
      }
    } { new MLProposalResource().accept("1", "false") }
    expect(204) { response.getStatus() }  // TODO
  }

  test("accept ID のパラメータが数値でない場合") {
    val response = new MLProposalResource().accept("a", "true")
    expect(400) { response.getStatus() }
  }

  test("accept ID のパラメータが null の場合") {
    val response = new MLProposalResource().accept(null, "true")
    expect(400) { response.getStatus() }
  }

  test("accept 承認するか(Boolean)のパラメータが Boolean でない場合") {
    val response = new MLProposalResource().accept("1", "a")
    expect(400) { response.getStatus() }
  }

  test("accept 承認するか(Boolean)のパラメータが null の場合") {
    val response = new MLProposalResource().accept("1", null)
    expect(400) { response.getStatus() }
  }

}