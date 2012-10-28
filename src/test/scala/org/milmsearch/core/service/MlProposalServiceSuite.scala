package org.milmsearch.core.service
import java.net.URL
import java.util.Calendar
import org.milmsearch.core.dao.NoSuchFieldException
import org.milmsearch.core.dao.MlProposalDao
import org.milmsearch.core.domain.MlArchiveType
import org.milmsearch.core.domain.CreateMlProposalRequest
import org.milmsearch.core.domain.MlProposal
import org.milmsearch.core.domain.MlProposalStatus
import org.milmsearch.core.domain.Page
import org.milmsearch.core.domain.Range
import org.milmsearch.core.domain.Sort
import org.milmsearch.core.domain.SortOrder
import org.milmsearch.core.ComponentRegistry
import org.scalamock.scalatest.MockFactory
import org.scalamock.ProxyMockFactory
import org.scalatest.FunSuite
import org.milmsearch.core.domain.Filter

class MlProposalServiceSuite extends FunSuite
    with MockFactory with ProxyMockFactory {

  test("create full") {
    val request = CreateMlProposalRequest(
      "申請者の名前",
      "proposer@example.com",
      "MLタイトル",
      MlProposalStatus.New,
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
  
  test("search") {
    val m = mock[MlProposalDao]
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

    m expects 'findAll withArgs(Range(20, 20), Sort(Symbol("id"), SortOrder.Ascending)) returning
        mlProposals.toList
    m expects 'count returning 100L

    val searchResult = ComponentRegistry.mlProposalDao.doWith(m) {
      new MlProposalServiceImpl().search(Page(2, 20), Sort(Symbol("id"), SortOrder.Ascending))
    }
    
    expect(100)(searchResult.totalResults)
    expect(21)(searchResult.startIndex)
    expect(20)(searchResult.itemsPerPage)
    expect(21)(searchResult.mlProposals.apply(0).id)
  }
  
  test("search when sortColumn is empty") {
    intercept[NoSuchFieldException] {
      new MlProposalServiceImpl().search(Page(1, 10), Sort(Symbol(""), SortOrder.Ascending))
    }
  }  

  test("search when sortColumn is invalid") {
    intercept[NoSuchFieldException] {
      new MlProposalServiceImpl().search(Page(1, 10), Sort(Symbol("hello"), SortOrder.Ascending))
    }
  }  

  test("search result is empty") {
    val m = mock[MlProposalDao]
    m expects 'findAll withArgs(Range(0, 10), Sort(Symbol("id"), SortOrder.Ascending)) returning
        Nil
    m expects 'count returning 0L

    val searchResult = ComponentRegistry.mlProposalDao.doWith(m) {
      new MlProposalServiceImpl().search(Page(1, 10), Sort(Symbol("id"), SortOrder.Ascending))
    }
    
    expect(0)(searchResult.totalResults)
    expect(1)(searchResult.startIndex)
    expect(0)(searchResult.itemsPerPage)
    expect(Nil)(searchResult.mlProposals)
  }  

  test("search result is 10 items then page is 1") {
    val m = mock[MlProposalDao]
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

    m expects 'findAll withArgs(Range(0, 10), Sort(Symbol("id"), SortOrder.Ascending)) returning
        mlProposals.toList
    m expects 'count returning 10L

    val searchResult = ComponentRegistry.mlProposalDao.doWith(m) {
      new MlProposalServiceImpl().search(Page(1, 10), Sort(Symbol("id"), SortOrder.Ascending))
    }
    
    expect(10)(searchResult.totalResults)
    expect(1)(searchResult.startIndex)
    expect(10)(searchResult.itemsPerPage)
    expect(1)(searchResult.mlProposals.apply(0).id)
  }

  test("search result is 10 items then page is 2") {
    val m = mock[MlProposalDao]
    m expects 'findAll withArgs(Range(10, 10), Sort(Symbol("id"), SortOrder.Ascending)) returning
        Nil
    m expects 'count returning 10L

    val searchResult = ComponentRegistry.mlProposalDao.doWith(m) {
      new MlProposalServiceImpl().search(Page(2, 10), Sort(Symbol("id"), SortOrder.Ascending))
    }
    
    expect(10)(searchResult.totalResults)
    expect(11)(searchResult.startIndex)
    expect(0)(searchResult.itemsPerPage)
    expect(Nil)(searchResult.mlProposals)
  }

  test("search result is 11 items then page is 1") {
    val m = mock[MlProposalDao]
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

    m expects 'findAll withArgs(Range(0, 10), Sort(Symbol("id"), SortOrder.Ascending)) returning
        mlProposals.toList
    m expects 'count returning 11L

    val searchResult = ComponentRegistry.mlProposalDao.doWith(m) {
      new MlProposalServiceImpl().search(Page(1, 10), Sort(Symbol("id"), SortOrder.Ascending))
    }
    
    expect(11)(searchResult.totalResults)
    expect(1)(searchResult.startIndex)
    expect(10)(searchResult.itemsPerPage)
    expect(1)(searchResult.mlProposals.apply(0).id)
  }
  
  test("search result is 11 items then page is 2") {
    val m = mock[MlProposalDao]
    val cal = Calendar.getInstance()
    cal.set(2012, Calendar.OCTOBER, 28, 10, 20, 30)
    val createdAt = cal.getTime()
    val mlProposals = for (i <- 11 to 11) yield MlProposal(
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

    m expects 'findAll withArgs(Range(10, 10), Sort(Symbol("id"), SortOrder.Ascending)) returning
        mlProposals.toList
    m expects 'count returning 11L

    val searchResult = ComponentRegistry.mlProposalDao.doWith(m) {
      new MlProposalServiceImpl().search(Page(2, 10), Sort(Symbol("id"), SortOrder.Ascending))
    }
    
    expect(11)(searchResult.totalResults)
    expect(11)(searchResult.startIndex)
    expect(1)(searchResult.itemsPerPage)
    expect(11)(searchResult.mlProposals.apply(0).id)
  }  
  
  test("search result is 21 items then page is 2") {
    val m = mock[MlProposalDao]
    val cal = Calendar.getInstance()
    cal.set(2012, Calendar.OCTOBER, 28, 10, 20, 30)
    val createdAt = cal.getTime()
    val mlProposals = for (i <- 11 to 20) yield MlProposal(
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

    m expects 'findAll withArgs(Range(10, 10), Sort(Symbol("id"), SortOrder.Ascending)) returning
        mlProposals.toList
    m expects 'count returning 21L

    val searchResult = ComponentRegistry.mlProposalDao.doWith(m) {
      new MlProposalServiceImpl().search(Page(2, 10), Sort(Symbol("id"), SortOrder.Ascending))
    }
    
    expect(21)(searchResult.totalResults)
    expect(11)(searchResult.startIndex)
    expect(10)(searchResult.itemsPerPage)
    expect(11)(searchResult.mlProposals.apply(0).id)
  }
  
  test("search by filter") {
    val m = mock[MlProposalDao]
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

    m expects 'findAll withArgs(Filter(Symbol("status"), "new"), 
        Range(20, 20), Sort(Symbol("id"), SortOrder.Ascending)) returning
        mlProposals.toList
    m expects 'count withArgs(Filter(Symbol("status"), "new")) returning 100L

    val searchResult = ComponentRegistry.mlProposalDao.doWith(m) {
      new MlProposalServiceImpl().search(Filter(Symbol("status"), "new"), 
        Page(2, 20), Sort(Symbol("id"), SortOrder.Ascending))
    }
    
    expect(100)(searchResult.totalResults)
    expect(21)(searchResult.startIndex)
    expect(20)(searchResult.itemsPerPage)
    expect(21)(searchResult.mlProposals.apply(0).id)
  }
  
  test("search by filter when filterColumn is empty") {
    intercept[NoSuchFieldException] {
      new MlProposalServiceImpl().search(Filter(Symbol(""), "new"), 
        Page(1, 10), Sort(Symbol("id"), SortOrder.Ascending))
    }
  }
  
  test("search by filter when filterColumn is mlTitle") {
    val m = mock[MlProposalDao]
    val cal = Calendar.getInstance()
    cal.set(2012, Calendar.OCTOBER, 28, 10, 20, 30)
    val createdAt = cal.getTime()
    val mlProposals = for (i <- 21 to 21) yield MlProposal(
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

    m expects 'findAll withArgs(Filter(Symbol("mlTitle"), "MLタイトル21"), 
        Range(0, 10), Sort(Symbol("id"), SortOrder.Ascending)) returning
        mlProposals.toList
    m expects 'count withArgs(Filter(Symbol("mlTitle"), "MLタイトル21")) returning 1L

    val searchResult = ComponentRegistry.mlProposalDao.doWith(m) {
      new MlProposalServiceImpl().search(Filter(Symbol("mlTitle"), "MLタイトル21"), 
        Page(1, 10), Sort(Symbol("id"), SortOrder.Ascending))
    }
    
    expect(1)(searchResult.totalResults)
    expect(1)(searchResult.startIndex)
    expect(1)(searchResult.itemsPerPage)
    expect(21)(searchResult.mlProposals.apply(0).id)
  }
  
  test("search by filter when filterValue is not enum value") {
    intercept[NoSuchFieldException] {
      new MlProposalServiceImpl().search(Filter(Symbol("status"), "hello"), 
        Page(1, 10), Sort(Symbol("id"), SortOrder.Ascending))
    }
  }
  
  test("search by filter when filterValue is empty") {
    intercept[SearchFailedException] {
      new MlProposalServiceImpl().search(Filter(Symbol("status"), ""), 
        Page(1, 10), Sort(Symbol("id"), SortOrder.Ascending))
    }
  }
  
  test("search by filter when filterColumn and filterValue are empty") {
    intercept[SearchFailedException] {
      new MlProposalServiceImpl().search(Filter(Symbol(""), ""), 
        Page(1, 10), Sort(Symbol("id"), SortOrder.Ascending))
    }
  }
  
  test("search by filter when sortColumn is empty") {
    intercept[NoSuchFieldException] {
      new MlProposalServiceImpl().search(Filter(Symbol("status"), "new"), 
        Page(1, 10), Sort(Symbol(""), SortOrder.Ascending))
    }
  }  

  test("search by filter when sortColumn is invalid") {
    intercept[NoSuchFieldException] {
      new MlProposalServiceImpl().search(Filter(Symbol("status"), "new"), 
        Page(1, 10), Sort(Symbol("hello"), SortOrder.Ascending))
    }
  }  

  test("search by filter result is empty") {
    val m = mock[MlProposalDao]
    m expects 'findAll withArgs(Filter(Symbol("status"), "new"), 
        Range(0, 10), Sort(Symbol("id"), SortOrder.Ascending)) returning
        Nil
    m expects 'count withArgs(Filter(Symbol("status"), "new")) returning 0L

    val searchResult = ComponentRegistry.mlProposalDao.doWith(m) {
      new MlProposalServiceImpl().search(Filter(Symbol("status"), "new"), 
        Page(1, 10), Sort(Symbol("id"), SortOrder.Ascending))
    }
    
    expect(0)(searchResult.totalResults)
    expect(1)(searchResult.startIndex)
    expect(0)(searchResult.itemsPerPage)
    expect(Nil)(searchResult.mlProposals)
  }  

  test("search by filter result is 10 items then page is 1") {
    val m = mock[MlProposalDao]
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

    m expects 'findAll withArgs(Filter(Symbol("status"), "new"), 
        Range(0, 10), Sort(Symbol("id"), SortOrder.Ascending)) returning
        mlProposals.toList
    m expects 'count withArgs(Filter(Symbol("status"), "new")) returning 10L

    val searchResult = ComponentRegistry.mlProposalDao.doWith(m) {
      new MlProposalServiceImpl().search(Filter(Symbol("status"), "new"), 
        Page(1, 10), Sort(Symbol("id"), SortOrder.Ascending))
    }
    
    expect(10)(searchResult.totalResults)
    expect(1)(searchResult.startIndex)
    expect(10)(searchResult.itemsPerPage)
    expect(1)(searchResult.mlProposals.apply(0).id)
  }

  test("search by filter result is 10 items then page is 2") {
    val m = mock[MlProposalDao]
    m expects 'findAll withArgs(Filter(Symbol("status"), "new"), 
        Range(10, 10), Sort(Symbol("id"), SortOrder.Ascending)) returning
        Nil
    m expects 'count withArgs(Filter(Symbol("status"), "new")) returning 10L

    val searchResult = ComponentRegistry.mlProposalDao.doWith(m) {
      new MlProposalServiceImpl().search(Filter(Symbol("status"), "new"), 
        Page(2, 10), Sort(Symbol("id"), SortOrder.Ascending))
    }
    
    expect(10)(searchResult.totalResults)
    expect(11)(searchResult.startIndex)
    expect(0)(searchResult.itemsPerPage)
    expect(Nil)(searchResult.mlProposals)
  }

  test("search by filter result is 11 items then page is 1") {
    val m = mock[MlProposalDao]
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

    m expects 'findAll withArgs(Filter(Symbol("status"), "new"), 
        Range(0, 10), Sort(Symbol("id"), SortOrder.Ascending)) returning
        mlProposals.toList
    m expects 'count withArgs(Filter(Symbol("status"), "new")) returning 11L

    val searchResult = ComponentRegistry.mlProposalDao.doWith(m) {
      new MlProposalServiceImpl().search(Filter(Symbol("status"), "new"), 
        Page(1, 10), Sort(Symbol("id"), SortOrder.Ascending))
    }
    
    expect(11)(searchResult.totalResults)
    expect(1)(searchResult.startIndex)
    expect(10)(searchResult.itemsPerPage)
    expect(1)(searchResult.mlProposals.apply(0).id)
  }
  
  test("search by filter result is 11 items then page is 2") {
    val m = mock[MlProposalDao]
    val cal = Calendar.getInstance()
    cal.set(2012, Calendar.OCTOBER, 28, 10, 20, 30)
    val createdAt = cal.getTime()
    val mlProposals = for (i <- 11 to 11) yield MlProposal(
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

    m expects 'findAll withArgs(Filter(Symbol("status"), "new"), 
        Range(10, 10), Sort(Symbol("id"), SortOrder.Ascending)) returning
        mlProposals.toList
    m expects 'count withArgs(Filter(Symbol("status"), "new")) returning 11L

    val searchResult = ComponentRegistry.mlProposalDao.doWith(m) {
      new MlProposalServiceImpl().search(Filter(Symbol("status"), "new"), 
        Page(2, 10), Sort(Symbol("id"), SortOrder.Ascending))
    }
    
    expect(11)(searchResult.totalResults)
    expect(11)(searchResult.startIndex)
    expect(1)(searchResult.itemsPerPage)
    expect(11)(searchResult.mlProposals.apply(0).id)
  }  
  
  test("search by filter result is 21 items then page is 2") {
    val m = mock[MlProposalDao]
    val cal = Calendar.getInstance()
    cal.set(2012, Calendar.OCTOBER, 28, 10, 20, 30)
    val createdAt = cal.getTime()
    val mlProposals = for (i <- 11 to 20) yield MlProposal(
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

    m expects 'findAll withArgs(Filter(Symbol("status"), "new"), 
        Range(10, 10), Sort(Symbol("id"), SortOrder.Ascending)) returning
        mlProposals.toList
    m expects 'count withArgs(Filter(Symbol("status"), "new")) returning 21L

    val searchResult = ComponentRegistry.mlProposalDao.doWith(m) {
      new MlProposalServiceImpl().search(Filter(Symbol("status"), "new"), 
        Page(2, 10), Sort(Symbol("id"), SortOrder.Ascending))
    }
    
    expect(21)(searchResult.totalResults)
    expect(11)(searchResult.startIndex)
    expect(10)(searchResult.itemsPerPage)
    expect(11)(searchResult.mlProposals.apply(0).id)
  }    
}