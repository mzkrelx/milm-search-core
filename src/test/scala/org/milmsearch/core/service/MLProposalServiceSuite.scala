package org.milmsearch.core.service
import java.net.URL
import org.milmsearch.core.dao.MLProposalDao
import org.milmsearch.core.dao.MLDao
import org.milmsearch.core.dao.NoSuchFieldException
import org.milmsearch.core.domain.CreateMLProposalRequest
import org.milmsearch.core.domain.CreateMLRequest
import org.milmsearch.core.domain.Filter
import org.milmsearch.core.domain.MlArchiveType
import org.milmsearch.core.domain.MLProposal
import org.milmsearch.core.domain.MLProposalColumn
import org.milmsearch.core.domain.{MLProposalFilterBy => MLPFilterBy}
import org.milmsearch.core.domain.{MLProposalSortBy => MLPSortBy}
import org.milmsearch.core.domain.{MLProposalStatus => MLPStatus}
import org.milmsearch.core.domain.Page
import org.milmsearch.core.domain.Range
import org.milmsearch.core.domain.Sort
import org.milmsearch.core.domain.SortOrder
import org.milmsearch.core.exception.DeleteFailedException
import org.milmsearch.core.exception.ResourceNotFoundException
import org.milmsearch.core.test.util.DateUtil
import org.milmsearch.core.test.util.MockCreatable
import org.milmsearch.core.ComponentRegistry
import org.milmsearch.core.{ComponentRegistry => CR}
import org.scalamock.scalatest.MockFactory
import org.scalamock.Mock
import org.scalamock.ProxyMockFactory
import org.scalatest.FunSuite
import org.joda.time.DateTime
import org.milmsearch.core.domain.UpdateMLProposalRequest

class MLProposalServiceSuite extends FunSuite
    with MockFactory with ProxyMockFactory with MockCreatable {

  test("create full") {
    val request = CreateMLProposalRequest(
      "申請者の名前",
      "proposer@example.com",
      "MLタイトル",
      MLPStatus.New,
      Some(MlArchiveType.Mailman),
      Some(new URL("http://localhost/path/to/archive/")),
      Some("コメント(MLの説明など)\nほげほげ)")
    )

    val m = mock[MLProposalDao]
    m expects 'create withArgs(request) returning 1L

    expect(1L) {
      ComponentRegistry.mlProposalDao.doWith(m) {
        new MLProposalServiceImpl().create(request)
      }
    }
  }



  test("search 検索条件なしで20件ずつの2ページ目のデータを検索") {
    val searchResult = ComponentRegistry.mlProposalDao.doWith(
      createMock[MLProposalDao] { m =>
        m expects 'findAll withArgs(Range(20, 20),
            Some(Sort(MLPSortBy.CreatedAt, SortOrder.Ascending)),
            None
          ) returning (21 to 40 map { i => MLProposal(
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
        new MLProposalServiceImpl().search(
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
      createMock[MLProposalDao] { m =>
        m expects 'findAll withArgs(Range(0, 10),
            Some(Sort(MLPSortBy.CreatedAt, SortOrder.Ascending)),
            None
          ) returning Nil
        m expects 'count returning 0L
      }) {
        new MLProposalServiceImpl().search(
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
      createMock[MLProposalDao] { m =>
        m expects 'findAll withArgs(Range(0, 10),
            Some(Sort(MLPSortBy.CreatedAt, SortOrder.Ascending)),
            None
          ) returning (1 to 10 map { i => MLProposal(
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
        new MLProposalServiceImpl().search(
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
      createMock[MLProposalDao] { m =>
        m expects 'findAll withArgs(Range(10, 10),
            Some(Sort(MLPSortBy.CreatedAt, SortOrder.Ascending)),
            None
          ) returning Nil
        m expects 'count returning 10L
      }) {
        new MLProposalServiceImpl().search(
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
      createMock[MLProposalDao] { m =>
        m expects 'findAll withArgs(Range(0, 10),
            Some(Sort(MLPSortBy.CreatedAt, SortOrder.Ascending)),
            None
          ) returning (1 to 10 map { i => MLProposal(
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
        new MLProposalServiceImpl().search(
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
      createMock[MLProposalDao] { m =>
        m expects 'findAll withArgs(Range(10, 10),
            Some(Sort(MLPSortBy.CreatedAt, SortOrder.Ascending)),
            None
          ) returning (11 to 11 map { i => MLProposal(
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
        new MLProposalServiceImpl().search(
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
      createMock[MLProposalDao] { m =>
        m expects 'findAll withArgs(Range(10, 10),
            Some(Sort(MLPSortBy.CreatedAt, SortOrder.Ascending)),
            None
          ) returning (11 to 20 map { i => MLProposal(
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
        new MLProposalServiceImpl().search(
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
      createMock[MLProposalDao] { m =>
        m expects 'findAll withArgs(
            Range(20, 20),
            Some(Sort(MLPSortBy.CreatedAt, SortOrder.Ascending)),
            Some(Filter(MLPFilterBy.Status, "new"))
          ) returning (21 to 40 map { i => MLProposal(
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
        new MLProposalServiceImpl().search(
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
      new MLProposalServiceImpl().search(
        filter = Some(Filter(MLPFilterBy.Status, "hello")),
        page   = Page(1, 10),
        sort   = Some(Sort(MLPSortBy.CreatedAt,
          SortOrder.Ascending)))
    }
  }

  test("search ステータスの検索値が空文字の場合") {
    intercept[NoSuchFieldException] {
      new MLProposalServiceImpl().search(
        filter = Some(Filter(MLPFilterBy.Status, "")),
        page   = Page(1, 10),
        sort   = Some(Sort(MLPSortBy.CreatedAt,
          SortOrder.Ascending)))
    }
  }

  test("search 検索条件を指定、結果が0件の場合") {
    val searchResult = ComponentRegistry.mlProposalDao.doWith(
      createMock[MLProposalDao] { m =>
        m expects 'findAll withArgs(
            Range(0, 10),
            Some(Sort(MLPSortBy.CreatedAt, SortOrder.Ascending)),
            Some(Filter(MLPFilterBy.Status, "new"))
          ) returning Nil
        m expects 'count returning 0L
      }) {
        new MLProposalServiceImpl().search(
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
      createMock[MLProposalDao] { m =>
        m expects 'findAll withArgs(
            Range(0, 10),
            Some(Sort(MLPSortBy.CreatedAt, SortOrder.Ascending)),
            Some(Filter(MLPFilterBy.Status, "new"))
          ) returning (1 to 10 map { i => MLProposal(
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
        new MLProposalServiceImpl().search(
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
      createMock[MLProposalDao] { m =>
        m expects 'findAll withArgs(
            Range(10, 10),
            Some(Sort(MLPSortBy.CreatedAt, SortOrder.Ascending)),
            Some(Filter(MLPFilterBy.Status, "new"))
          ) returning Nil
        m expects 'count withArgs(
            Some(Filter(MLPFilterBy.Status, "new"))
          ) returning 10L
      }) {
        new MLProposalServiceImpl().search(
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
      createMock[MLProposalDao] { m =>
        m expects 'findAll withArgs(
            Range(0, 10),
            Some(Sort(MLPSortBy.CreatedAt, SortOrder.Ascending)),
            Some(Filter(MLPFilterBy.Status, "new"))
          ) returning (1 to 10 map { i => MLProposal(
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
        new MLProposalServiceImpl().search(
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
      createMock[MLProposalDao] { m =>
        m expects 'findAll withArgs(
            Range(10, 10),
            Some(Sort(MLPSortBy.CreatedAt, SortOrder.Ascending)),
            Some(Filter(MLPFilterBy.Status, "new"))
          ) returning (11 to 11 map { i => MLProposal(
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
        new MLProposalServiceImpl().search(
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
      createMock[MLProposalDao] { m =>
        m expects 'findAll withArgs(
            Range(10, 10),
            Some(Sort(MLPSortBy.CreatedAt, SortOrder.Ascending)),
            Some(Filter(MLPFilterBy.Status, "new"))
          ) returning (11 to 20 map { i => MLProposal(
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
        new MLProposalServiceImpl().search(
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

  test("delete_正常") {
    val id = 1L

    val m = mock[MLProposalDao]
    m expects 'delete withArgs (1L) returning true

    ComponentRegistry.mlProposalDao.doWith(m) {
    	new MLProposalServiceImpl().delete(id)
    }
  }

  test("delete_ID該当なし") {
    val id = 1L

    val m = mock[MLProposalDao]
    m expects 'delete withArgs (1L) returning false

    intercept[ResourceNotFoundException] {
      ComponentRegistry.mlProposalDao.doWith(m) {
        new MLProposalServiceImpl().delete(id)
      }
    }
  }

  test("delete_サーバエラー") {
    val id = 1L

    val m = mock[MLProposalDao]
    m expects 'delete withArgs (1L) throws new DeleteFailedException("Delete failed.")

    intercept[DeleteFailedException] {
      ComponentRegistry.mlProposalDao.doWith(m) {
        new MLProposalServiceImpl().delete(id)
      }
    }
  }

  test("find") {
    val mlp = ComponentRegistry.mlProposalDao.doWith(
      createMock[MLProposalDao] { m =>
        m expects 'find withArgs(1) returning (Option(
          MLProposal(
            1,
            "申請者の名前",
            "proposer@example.com",
            "MLタイトル",
            MLPStatus.New,
            Some(MlArchiveType.Mailman),
            Some(new URL("http://localhost/path/to/archive/")),
            Some("コメント(MLの説明など)"),
            DateUtil.createDate("2012/10/28 10:20:30"),
            DateUtil.createDate("2012/10/28 10:20:30"))))
      }) {
        new MLProposalServiceImpl().find(1)
      }

    expect(1)(mlp.get.id)
  }

  test("update") {
    import MLProposalColumn._
    ComponentRegistry.mlProposalDao.doWith {
      createMock[MLProposalDao] {
        _ expects 'update withArgs(1, List((MlTitle, "new Title"),
          (ArchiveType, MlArchiveType.Other),
          (ArchiveURL, new URL("http://newurl")))) returning true
      }
    } {
      new MLProposalServiceImpl().update(1,
        UpdateMLProposalRequest(
          "new Title",
          MlArchiveType.Other,
          new URL("http://newurl")))
    }
  }

  test("accept") {
    val now = new DateTime(1986, 11, 28, 10, 12, 13, 0)
    ComponentRegistry.dateTimeService.doWith {
      createMock[DateTimeService] {
        _ expects 'now returning now
      }
    } {
      ComponentRegistry.mlProposalDao.doWith {
        createMock[MLProposalDao] { m =>
          m expects 'update withArgs(
            1, List(
            (MLProposalColumn.Status, MLPStatus.Accepted.toString),
            (MLProposalColumn.JudgedAt, now.toDate()))
          ) returning (true)
          m expects 'find withArgs(1L) returning Some(
            MLProposal(
              1,
              "申請者の名前",
              "proposer@example.com",
              "MLタイトル",
              MLPStatus.Accepted,
              Some(MlArchiveType.Mailman),
              Some(new URL("http://localhost/path/to/archive/")),
              Some("コメント(MLの説明など)"),
              DateUtil.createDate("2012/10/28 10:20:30"),
              DateUtil.createDate("2012/10/28 10:20:30"),
              Some(now.toDate)))
        }
      } {
        CR.mlDao.doWith {
          createMock[MLDao] {
            _ expects 'create withArgs(
              CreateMLRequest(
                "MLタイトル",
                MlArchiveType.Mailman,
                new URL("http://localhost/path/to/archive/"),
                now)) returning 10L
          }
        } { new MLProposalServiceImpl().accept(1) }
      }
    }
  }

  test("accept 承認する対象が無かった場合") {
    val now = new DateTime(1986, 11, 28, 10, 12, 13, 0)
    intercept[ResourceNotFoundException] {
      ComponentRegistry.dateTimeService.doWith {
        createMock[DateTimeService] {
          _ expects 'now returning now
        }
      } {
        ComponentRegistry.mlProposalDao.doWith {
          createMock[MLProposalDao] {
            _ expects 'update withArgs(
              1, List(
              (MLProposalColumn.Status, MLPStatus.Accepted.toString),
              (MLProposalColumn.JudgedAt, now.toDate()))
            ) returning (false)
          }
        } {
            new MLProposalServiceImpl().accept(1)
          }
      }
    }
  }

  test("reject") {
    val now = new DateTime(1986, 11, 28, 10, 12, 13, 0)
    ComponentRegistry.dateTimeService.doWith {
      createMock[DateTimeService] {
        _ expects 'now returning now
      }
    } {
      ComponentRegistry.mlProposalDao.doWith {
        createMock[MLProposalDao] {
          _ expects 'update withArgs(
            1, List(
            (MLProposalColumn.Status, MLPStatus.Rejected.toString),
            (MLProposalColumn.JudgedAt, now.toDate()))
          ) returning (true)
        }
      } {
          new MLProposalServiceImpl().reject(1)
        }
    }
  }

  test("reject 却下する対象が無かった場合") {
    val now = new DateTime(1986, 11, 28, 10, 12, 13, 0)
    intercept[ResourceNotFoundException] {
      ComponentRegistry.dateTimeService.doWith {
        createMock[DateTimeService] {
          _ expects 'now returning now
        }
      } {
        ComponentRegistry.mlProposalDao.doWith {
          createMock[MLProposalDao] {
            _ expects 'update withArgs(
              1, List(
              (MLProposalColumn.Status, MLPStatus.Rejected.toString),
              (MLProposalColumn.JudgedAt, now.toDate()))
            ) returning (false)
          }
        } {
            new MLProposalServiceImpl().reject(1)
          }
      }
    }
  }
}