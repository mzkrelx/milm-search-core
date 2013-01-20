package org.milmsearch.core.service
import java.net.URL
import java.util.Calendar
import org.milmsearch.core.dao.NoSuchFieldException
import org.milmsearch.core.dao.MlProposalDao
import org.milmsearch.core.domain.MlArchiveType
import org.milmsearch.core.domain.CreateMlProposalRequest
import org.milmsearch.core.domain.MlProposal
import org.milmsearch.core.domain.Page
import org.milmsearch.core.domain.Range
import org.milmsearch.core.domain.Sort
import org.milmsearch.core.domain.SortOrder
import org.milmsearch.core.ComponentRegistry
import org.scalamock.scalatest.MockFactory
import org.scalamock.ProxyMockFactory
import org.scalamock.Mock
import org.scalatest.FunSuite
import org.milmsearch.core.domain.Filter
import org.milmsearch.core.domain.{MlProposalStatus => MLPStatus}
import org.milmsearch.core.domain.{MlProposalSortBy => MLPSortBy}
import org.milmsearch.core.domain.{MlProposalFilterBy => MLPFilterBy}
import org.milmsearch.core.test.util.MockCreatable
import org.milmsearch.core.test.util.DateUtil
import org.milmsearch.core.exception.ResourceNotFoundException
import org.milmsearch.core.exception.DeleteFailedException

class MlProposalServiceSuite extends FunSuite
    with MockFactory with ProxyMockFactory with MockCreatable {

  test("create full") {
    val request = CreateMlProposalRequest(
      "申請者の名前",
      "proposer@example.com",
      "MLタイトル",
      MLPStatus.New,
      Some(MlArchiveType.Mailman),
      Some(new URL("http://localhost/path/to/archive/")),
      Some("コメント(MLの説明など)\nほげほげ)")
    )

    val m = mock[MlProposalDao]
    m expects 'create withArgs(request) returning 1L

    expect(1L) {
      ComponentRegistry.mlProposalDao.doWith(m) {
        new MlProposalServiceImpl().create(request)
      }
    }
  }

  test("delete_正常") { ////
    // mockは戻り値なしで良い。戻り値がない場合のexpectの書き方は後ほど
    val id = 1L

    val m = mock[MlProposalDao]
    m expects 'delete withArgs (1L) returning true

    expect(true) {
      ComponentRegistry.mlProposalDao.doWith(m) {
        new MlProposalServiceImpl().delete(id)
      }
    }
  }

  test("delete_id該当なし") { ////
    // mockは戻り値なしで良い。
    val id = 1L

    val m = mock[MlProposalDao]
    m expects 'delete withArgs (1L) returning false

    expect(false) {
      ComponentRegistry.mlProposalDao.doWith(m) {
        new MlProposalServiceImpl().delete(id)
      }
    }
  }

  test("search 検索条件なしで20件ずつの2ページ目のデータを検索") {
    val searchResult = ComponentRegistry.mlProposalDao.doWith(
      createMock[MlProposalDao] { m =>
        m expects 'findAll withArgs(Range(20, 20),
            Some(Sort(MLPSortBy.CreatedAt, SortOrder.Ascending)),
            None
          ) returning (21 to 40 map { i => MlProposal(
            i,
            "申請者の名前",
            "proposer@example.com",
            "MLタイトル" + i,
            MLPStatus.New,
            Some(MlArchiveType.Mailman),
            Some(new URL("http://localhost/path/to/archive/")),
            Some("コメント(MLの説明など)"),
            DateUtil.createDate("2012/10/28 10:20:30"),
            DateUtil.createDate("2012/10/28 10:20:30"))
          } toList)
        m expects 'count returning 100L
      }) {
        new MlProposalServiceImpl().search(
          filter = None,
          page   = Page(2, 20),
          sort   = Some(Sort(MLPSortBy.CreatedAt,
            SortOrder.Ascending)))
      }

    expect(100)(searchResult.totalResults)
    expect(21)(searchResult.startIndex)
    expect(20)(searchResult.itemsPerPage)
    expect(21)(searchResult.mlProposals.apply(0).id)
  }

  test("search 検索結果がないとき") {
    val searchResult = ComponentRegistry.mlProposalDao.doWith(
      createMock[MlProposalDao] { m =>
        m expects 'findAll withArgs(Range(0, 10),
            Some(Sort(MLPSortBy.CreatedAt, SortOrder.Ascending)),
            None
          ) returning Nil
        m expects 'count returning 0L
      }) {
        new MlProposalServiceImpl().search(
          filter = None,
          page   = Page(1, 10),
          sort   = Some(Sort(MLPSortBy.CreatedAt,
            SortOrder.Ascending)))
      }

    expect(0)(searchResult.totalResults)
    expect(1)(searchResult.startIndex)
    expect(0)(searchResult.itemsPerPage)
    expect(Nil)(searchResult.mlProposals)
  }

  test("search 検索結果が10件、10件ずつ1ページ目のデータを検索") {
    val searchResult = ComponentRegistry.mlProposalDao.doWith(
      createMock[MlProposalDao] { m =>
        m expects 'findAll withArgs(Range(0, 10),
            Some(Sort(MLPSortBy.CreatedAt, SortOrder.Ascending)),
            None
          ) returning (1 to 10 map { i => MlProposal(
            i,
            "申請者の名前",
            "proposer@example.com",
            "MLタイトル" + i,
            MLPStatus.New,
            Some(MlArchiveType.Mailman),
            Some(new URL("http://localhost/path/to/archive/")),
            Some("コメント(MLの説明など)"),
            DateUtil.createDate("2012/10/28 10:20:30"),
            DateUtil.createDate("2012/10/28 10:20:30"))
          } toList)
        m expects 'count returning 10L
      }) {
        new MlProposalServiceImpl().search(
          filter = None,
          page   = Page(1, 10),
          sort   = Some(Sort(MLPSortBy.CreatedAt,
            SortOrder.Ascending)))
      }

    expect(10)(searchResult.totalResults)
    expect(1)(searchResult.startIndex)
    expect(10)(searchResult.itemsPerPage)
    expect(1)(searchResult.mlProposals.apply(0).id)
  }

  test("search 検索結果が10件、10件ずつ2ページ目のデータを検索") {
    val searchResult = ComponentRegistry.mlProposalDao.doWith(
      createMock[MlProposalDao] { m =>
        m expects 'findAll withArgs(Range(10, 10),
            Some(Sort(MLPSortBy.CreatedAt, SortOrder.Ascending)),
            None
          ) returning Nil
        m expects 'count returning 10L
      }) {
        new MlProposalServiceImpl().search(
          filter = None,
          page   = Page(2, 10),
          sort   = Some(Sort(MLPSortBy.CreatedAt,
            SortOrder.Ascending)))
      }

    expect(10)(searchResult.totalResults)
    expect(11)(searchResult.startIndex)
    expect(0)(searchResult.itemsPerPage)
    expect(Nil)(searchResult.mlProposals)
  }

  test("search 検索結果が11件、10件ずつ1ページ目のデータを検索") {
    val searchResult = ComponentRegistry.mlProposalDao.doWith(
      createMock[MlProposalDao] { m =>
        m expects 'findAll withArgs(Range(0, 10),
            Some(Sort(MLPSortBy.CreatedAt, SortOrder.Ascending)),
            None
          ) returning (1 to 10 map { i => MlProposal(
            i,
            "申請者の名前",
            "proposer@example.com",
            "MLタイトル" + i,
            MLPStatus.New,
            Some(MlArchiveType.Mailman),
            Some(new URL("http://localhost/path/to/archive/")),
            Some("コメント(MLの説明など)"),
            DateUtil.createDate("2012/10/28 10:20:30"),
            DateUtil.createDate("2012/10/28 10:20:30"))
          } toList)
        m expects 'count returning 11L
      }) {
        new MlProposalServiceImpl().search(
          filter = None,
          page   = Page(1, 10),
          sort   = Some(Sort(MLPSortBy.CreatedAt,
            SortOrder.Ascending)))
      }

    expect(11)(searchResult.totalResults)
    expect(1)(searchResult.startIndex)
    expect(10)(searchResult.itemsPerPage)
    expect(1)(searchResult.mlProposals.apply(0).id)
  }

  test("search 検索結果が11件、10件ずつ2ページ目のデータを検索") {
    val searchResult = ComponentRegistry.mlProposalDao.doWith(
      createMock[MlProposalDao] { m =>
        m expects 'findAll withArgs(Range(10, 10),
            Some(Sort(MLPSortBy.CreatedAt, SortOrder.Ascending)),
            None
          ) returning (11 to 11 map { i => MlProposal(
            i,
            "申請者の名前",
            "proposer@example.com",
            "MLタイトル" + i,
            MLPStatus.New,
            Some(MlArchiveType.Mailman),
            Some(new URL("http://localhost/path/to/archive/")),
            Some("コメント(MLの説明など)"),
            DateUtil.createDate("2012/10/28 10:20:30"),
            DateUtil.createDate("2012/10/28 10:20:30"))
          } toList)
        m expects 'count returning 11L
      }) {
        new MlProposalServiceImpl().search(
          filter = None,
          page   = Page(2, 10),
          sort   = Some(Sort(MLPSortBy.CreatedAt,
            SortOrder.Ascending)))
      }

    expect(11)(searchResult.totalResults)
    expect(11)(searchResult.startIndex)
    expect(1)(searchResult.itemsPerPage)
    expect(11)(searchResult.mlProposals.apply(0).id)
  }

  test("search 検索結果が21件、10件ずつ2ページ目のデータを検索") {
    val searchResult = ComponentRegistry.mlProposalDao.doWith(
      createMock[MlProposalDao] { m =>
        m expects 'findAll withArgs(Range(10, 10),
            Some(Sort(MLPSortBy.CreatedAt, SortOrder.Ascending)),
            None
          ) returning (11 to 20 map { i => MlProposal(
            i,
            "申請者の名前",
            "proposer@example.com",
            "MLタイトル" + i,
            MLPStatus.New,
            Some(MlArchiveType.Mailman),
            Some(new URL("http://localhost/path/to/archive/")),
            Some("コメント(MLの説明など)"),
            DateUtil.createDate("2012/10/28 10:20:30"),
            DateUtil.createDate("2012/10/28 10:20:30"))
          } toList)
        m expects 'count returning 21L
      }) {
        new MlProposalServiceImpl().search(
          filter = None,
          page   = Page(2, 10),
          sort   = Some(Sort(MLPSortBy.CreatedAt,
            SortOrder.Ascending)))
      }

    expect(21)(searchResult.totalResults)
    expect(11)(searchResult.startIndex)
    expect(10)(searchResult.itemsPerPage)
    expect(11)(searchResult.mlProposals.apply(0).id)
  }

  test("search 検索条件を指定した検索") {
    val searchResult = ComponentRegistry.mlProposalDao.doWith(
      createMock[MlProposalDao] { m =>
        m expects 'findAll withArgs(
            Range(20, 20),
            Some(Sort(MLPSortBy.CreatedAt, SortOrder.Ascending)),
            Some(Filter(MLPFilterBy.Status, "new"))
          ) returning (21 to 40 map { i => MlProposal(
            i,
            "申請者の名前",
            "proposer@example.com",
            "MLタイトル" + i,
            MLPStatus.New,
            Some(MlArchiveType.Mailman),
            Some(new URL("http://localhost/path/to/archive/")),
            Some("コメント(MLの説明など)"),
            DateUtil.createDate("2012/10/28 10:20:30"),
            DateUtil.createDate("2012/10/28 10:20:30"))
          } toList)

        m expects 'count withArgs(
            Some(Filter(MLPFilterBy.Status, "new"))
          ) returning 100L
      }) {
        new MlProposalServiceImpl().search(
          filter = Some(Filter(MLPFilterBy.Status, "new")),
          page   = Page(2, 20),
          sort   = Some(Sort(MLPSortBy.CreatedAt,
            SortOrder.Ascending)))
      }

    expect(100)(searchResult.totalResults)
    expect(21)(searchResult.startIndex)
    expect(20)(searchResult.itemsPerPage)
    expect(21)(searchResult.mlProposals.apply(0).id)
  }

  test("search ステータスの検索値が存在しない値の場合") {
    intercept[NoSuchFieldException] {
      new MlProposalServiceImpl().search(
        filter = Some(Filter(MLPFilterBy.Status, "hello")),
        page   = Page(1, 10),
        sort   = Some(Sort(MLPSortBy.CreatedAt,
          SortOrder.Ascending)))
    }
  }

  test("search ステータスの検索値が空文字の場合") {
    intercept[NoSuchFieldException] {
      new MlProposalServiceImpl().search(
        filter = Some(Filter(MLPFilterBy.Status, "")),
        page   = Page(1, 10),
        sort   = Some(Sort(MLPSortBy.CreatedAt,
          SortOrder.Ascending)))
    }
  }

  test("search 検索条件を指定、結果が0件の場合") {
    val searchResult = ComponentRegistry.mlProposalDao.doWith(
      createMock[MlProposalDao] { m =>
        m expects 'findAll withArgs(
            Range(0, 10),
            Some(Sort(MLPSortBy.CreatedAt, SortOrder.Ascending)),
            Some(Filter(MLPFilterBy.Status, "new"))
          ) returning Nil
        m expects 'count returning 0L
      }) {
        new MlProposalServiceImpl().search(
          filter = Some(Filter(MLPFilterBy.Status, "new")),
          page   = Page(1, 10),
          sort   = Some(Sort(MLPSortBy.CreatedAt,
            SortOrder.Ascending)))
      }

    expect(0)(searchResult.totalResults)
    expect(1)(searchResult.startIndex)
    expect(0)(searchResult.itemsPerPage)
    expect(Nil)(searchResult.mlProposals)
  }

  test("search 検索条件を指定、検索結果が10件、10件ずつ1ページ目のデータを検索") {
    val searchResult = ComponentRegistry.mlProposalDao.doWith(
      createMock[MlProposalDao] { m =>
        m expects 'findAll withArgs(
            Range(0, 10),
            Some(Sort(MLPSortBy.CreatedAt, SortOrder.Ascending)),
            Some(Filter(MLPFilterBy.Status, "new"))
          ) returning (1 to 10 map { i => MlProposal(
            i,
            "申請者の名前",
            "proposer@example.com",
            "MLタイトル" + i,
            MLPStatus.New,
            Some(MlArchiveType.Mailman),
            Some(new URL("http://localhost/path/to/archive/")),
            Some("コメント(MLの説明など)"),
            DateUtil.createDate("2012/10/28 10:20:30"),
            DateUtil.createDate("2012/10/28 10:20:30"))
          } toList)
        m expects 'count withArgs(
            Some(Filter(MLPFilterBy.Status, "new"))
          ) returning 10L
      }) {
        new MlProposalServiceImpl().search(
          filter = Some(Filter(MLPFilterBy.Status, "new")),
          page   = Page(1, 10),
          sort   = Some(Sort(MLPSortBy.CreatedAt,
            SortOrder.Ascending)))
      }

    expect(10)(searchResult.totalResults)
    expect(1)(searchResult.startIndex)
    expect(10)(searchResult.itemsPerPage)
    expect(1)(searchResult.mlProposals.apply(0).id)
  }

  test("search 検索条件を指定、検索結果が10件、10件ずつ2ページ目のデータを検索") {
    val searchResult = ComponentRegistry.mlProposalDao.doWith(
      createMock[MlProposalDao] { m =>
        m expects 'findAll withArgs(
            Range(10, 10),
            Some(Sort(MLPSortBy.CreatedAt, SortOrder.Ascending)),
            Some(Filter(MLPFilterBy.Status, "new"))
          ) returning Nil
        m expects 'count withArgs(
            Some(Filter(MLPFilterBy.Status, "new"))
          ) returning 10L
      }) {
        new MlProposalServiceImpl().search(
          filter = Some(Filter(MLPFilterBy.Status, "new")),
          page   = Page(2, 10),
          sort   = Some(Sort(MLPSortBy.CreatedAt,
            SortOrder.Ascending)))
      }

    expect(10)(searchResult.totalResults)
    expect(11)(searchResult.startIndex)
    expect(0)(searchResult.itemsPerPage)
    expect(Nil)(searchResult.mlProposals)
  }

  test("search 検索条件を指定、検索結果が11件、10件ずつ1ページ目のデータを検索") {
    val searchResult = ComponentRegistry.mlProposalDao.doWith(
      createMock[MlProposalDao] { m =>
        m expects 'findAll withArgs(
            Range(0, 10),
            Some(Sort(MLPSortBy.CreatedAt, SortOrder.Ascending)),
            Some(Filter(MLPFilterBy.Status, "new"))
          ) returning (1 to 10 map { i => MlProposal(
            i,
            "申請者の名前",
            "proposer@example.com",
            "MLタイトル" + i,
            MLPStatus.New,
            Some(MlArchiveType.Mailman),
            Some(new URL("http://localhost/path/to/archive/")),
            Some("コメント(MLの説明など)"),
            DateUtil.createDate("2012/10/28 10:20:30"),
            DateUtil.createDate("2012/10/28 10:20:30"))
          } toList)
        m expects 'count withArgs(
            Some(Filter(MLPFilterBy.Status, "new"))
          ) returning 11L
      }) {
        new MlProposalServiceImpl().search(
          filter = Some(Filter(MLPFilterBy.Status, "new")),
          page   = Page(1, 10),
          sort   = Some(Sort(MLPSortBy.CreatedAt,
            SortOrder.Ascending)))
      }

    expect(11)(searchResult.totalResults)
    expect(1)(searchResult.startIndex)
    expect(10)(searchResult.itemsPerPage)
    expect(1)(searchResult.mlProposals.apply(0).id)
  }

  test("search 検索条件を指定、検索結果が11件、10件ずつ2ページ目のデータを検索") {
    val searchResult = ComponentRegistry.mlProposalDao.doWith(
      createMock[MlProposalDao] { m =>
        m expects 'findAll withArgs(
            Range(10, 10),
            Some(Sort(MLPSortBy.CreatedAt, SortOrder.Ascending)),
            Some(Filter(MLPFilterBy.Status, "new"))
          ) returning (11 to 11 map { i => MlProposal(
            i,
            "申請者の名前",
            "proposer@example.com",
            "MLタイトル" + i,
            MLPStatus.New,
            Some(MlArchiveType.Mailman),
            Some(new URL("http://localhost/path/to/archive/")),
            Some("コメント(MLの説明など)"),
            DateUtil.createDate("2012/10/28 10:20:30"),
            DateUtil.createDate("2012/10/28 10:20:30"))
          } toList)
        m expects 'count withArgs(
            Some(Filter(MLPFilterBy.Status, "new"))
          ) returning 11L
      }) {
        new MlProposalServiceImpl().search(
          filter = Some(Filter(MLPFilterBy.Status, "new")),
          page   = Page(2, 10),
          sort   = Some(Sort(MLPSortBy.CreatedAt,
            SortOrder.Ascending)))
      }

    expect(11)(searchResult.totalResults)
    expect(11)(searchResult.startIndex)
    expect(1)(searchResult.itemsPerPage)
    expect(11)(searchResult.mlProposals.apply(0).id)
  }

  test("search 検索条件を指定、検索結果が21件、10件ずつ2ページ目のデータを検索") {
    val searchResult = ComponentRegistry.mlProposalDao.doWith(
      createMock[MlProposalDao] { m =>
        m expects 'findAll withArgs(
            Range(10, 10),
            Some(Sort(MLPSortBy.CreatedAt, SortOrder.Ascending)),
            Some(Filter(MLPFilterBy.Status, "new"))
          ) returning (11 to 20 map { i => MlProposal(
            i,
            "申請者の名前",
            "proposer@example.com",
            "MLタイトル" + i,
            MLPStatus.New,
            Some(MlArchiveType.Mailman),
            Some(new URL("http://localhost/path/to/archive/")),
            Some("コメント(MLの説明など)"),
            DateUtil.createDate("2012/10/28 10:20:30"),
            DateUtil.createDate("2012/10/28 10:20:30"))
          } toList)
        m expects 'count withArgs(
            Some(Filter(MLPFilterBy.Status, "new"))
          ) returning 21L
      }) {
        new MlProposalServiceImpl().search(
          filter = Some(Filter(MLPFilterBy.Status, "new")),
          page   = Page(2, 10),
          sort   = Some(Sort(MLPSortBy.CreatedAt,
            SortOrder.Ascending)))
      }

    expect(21)(searchResult.totalResults)
    expect(11)(searchResult.startIndex)
    expect(10)(searchResult.itemsPerPage)
    expect(11)(searchResult.mlProposals.apply(0).id)
  }
  
  test("delete_ID該当なし") { ////
    // mock は false (Not Found) を返す
    val id = 1L

    val m = mock[MlProposalDao]
    m expects 'delete withArgs (1L) returning false

    intercept[ResourceNotFoundException] {
      ComponentRegistry.mlProposalDao.doWith(m) {
        new MlProposalServiceImpl().delete(id)
      }
    }
  }
  
  test("delete_サーバエラー") { ////
    // mock は例外を発生する
    val id = 1L

    val m = mock[MlProposalDao]
    m expects 'delete withArgs (1L) throws new DeleteFailedException("Delete failed.")

    intercept[DeleteFailedException] {
      ComponentRegistry.mlProposalDao.doWith(m) {
        new MlProposalServiceImpl().delete(id)
      }
    }
  }

}