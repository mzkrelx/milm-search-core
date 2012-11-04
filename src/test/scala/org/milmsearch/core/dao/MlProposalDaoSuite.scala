package org.milmsearch.core.dao
import org.milmsearch.core.domain.Filter
import org.milmsearch.core.domain.MlArchiveType
import org.milmsearch.core.domain.MlProposalFilterBy
import org.milmsearch.core.domain.{MlProposalFilterBy => MLPFBy}
import org.milmsearch.core.domain.{MlProposalSortBy => MLPSBy}
import org.milmsearch.core.domain.MlProposalStatus
import org.milmsearch.core.domain.Range
import org.milmsearch.core.domain.Sort
import org.milmsearch.core.domain.SortOrder
import org.milmsearch.core.Bootstrap
import org.scalatest.BeforeAndAfterAll
import org.scalatest.FunSuite
import mapper.{MlProposalMetaMapper => MLPMMapper}
import net.liftweb.mapper.Ascending
import net.liftweb.mapper.By
import net.liftweb.mapper.DB
import net.liftweb.mapper.OrderBy
import net.liftweb.mapper.Schemifier
import java.net.URL
import org.apache.commons.lang3.time.DateFormatUtils
import org.scalatest.BeforeAndAfter

class MlProposalDaoSuite extends FunSuite with BeforeAndAfterAll with BeforeAndAfter {
  // TODO
  test("insert full") { pending }
  
  /**
   * 全てのテストの前処理
   */
  override def beforeAll() {
    Schemifier.schemify(true, Schemifier.infoF _, MLPMMapper)
  }
  
  after {
    DB.runUpdate("TRUNCATE TABLE ML_PROPOSAL", Nil)
  }
  
  test("toBy status accepted") {
    expect(By(MLPMMapper.status, MlProposalStatus.Accepted)) {
      new MlProposalDaoImpl().toBy(Filter(MLPFBy.Status, MlProposalStatus.Accepted))
    }
  }
  
  test("toOrderBy id asc") {
    expect(OrderBy(MLPMMapper.id, Ascending)) {
      new MlProposalDaoImpl().toOrderBy(Sort(MLPSBy.Id, SortOrder.Ascending))
    }
  }
  
  test("findAll") {
    DB.runUpdate("INSERT INTO ML_PROPOSAL VALUES(?,?,?,?,?,?,?,?,?,?)", 
        List(1, "name1", "sample@sample.com", "title", 
          1, 1, "http://sample.com", "message",
          "2012-10-10 10:10:11", "2012-10-11 10:10:11"
        )
    )
    val mp = new MlProposalDaoImpl().findAll(Range(0, 10),
        Sort(MLPSBy.Id, SortOrder.Ascending)).head
    expect(1) {
      mp.id
    }
    expect("name1") {
      mp.proposerName
    }
    expect("sample@sample.com") {
      mp.proposerEmail
    }
    expect("title") {
      mp.mlTitle
    }
    expect(MlProposalStatus.Accepted) {
      mp.status
    }
    expect(Some(MlArchiveType.Other)) {
      mp.archiveType
    }
    expect(Some(new URL("http://sample.com"))) {
      mp.archiveUrl
    }
    expect(Some("message")) {
      mp.comment
    }
    expect("2012-10-10T10:10:11") {
      DateFormatUtils.ISO_DATETIME_FORMAT.format(mp.createdAt)
    }
    expect("2012-10-11T10:10:11") {
      DateFormatUtils.ISO_DATETIME_FORMAT.format(mp.updatedAt)
    }    
  }

  test("findAll filter range sort") {
    DB.runUpdate("INSERT INTO ML_PROPOSAL VALUES(?,?,?,?,?,?,?,?,?,?)", 
        List(1, "name1", "sample@sample.com", "title", 
          1, 1, "http://sample.com", "message",
          "2012-10-10 10:10:11", "2012-10-11 10:10:11"
        )
    )
    DB.runUpdate("INSERT INTO ML_PROPOSAL VALUES(?,?,?,?,?,?,?,?,?,?)", 
        List(2, "name2", "sample2@sample.com", "title2", 
          2, 1, "http://sample.com2", "message2",
          "2012-10-10 10:10:11", "2012-10-11 10:10:11"
        )
    )
    DB.runUpdate("INSERT INTO ML_PROPOSAL VALUES(?,?,?,?,?,?,?,?,?,?)", 
        List(3, "name3", "sample3@sample.com", "title3", 
          2, 1, "http://sample.com3", "message3",
          "2012-10-10 10:10:11", "2012-10-11 10:10:11"
        )
    )
    val mps = new MlProposalDaoImpl().findAll(
        Filter(MLPFBy.Status, MlProposalStatus.Rejected), 
        Range(0, 10),
        Sort(MLPSBy.Id, SortOrder.Descending))
    expect(2) {
      mps.length
    }
    expect(3) {
      mps.head.id
    }
    expect(2) {
      mps.tail.head.id
    }
  }
  
  test("findAll range") {
    DB.runUpdate("INSERT INTO ML_PROPOSAL VALUES(?,?,?,?,?,?,?,?,?,?)", 
        List(1, "name1", "sample@sample.com", "title", 
          1, 1, "http://sample.com", "message",
          "2012-10-10 10:10:11", "2012-10-11 10:10:11"
        )
    )
    DB.runUpdate("INSERT INTO ML_PROPOSAL VALUES(?,?,?,?,?,?,?,?,?,?)", 
        List(2, "name2", "sample2@sample.com", "title2", 
          2, 1, "http://sample.com2", "message2",
          "2012-10-10 10:10:11", "2012-10-11 10:10:11"
        )
    )
    DB.runUpdate("INSERT INTO ML_PROPOSAL VALUES(?,?,?,?,?,?,?,?,?,?)", 
        List(3, "name3", "sample3@sample.com", "title3", 
          2, 1, "http://sample.com3", "message3",
          "2012-10-10 10:10:11", "2012-10-11 10:10:11"
        )
    )
    val mps = new MlProposalDaoImpl().findAll(
        Range(1, 1),
        Sort(MLPSBy.Id, SortOrder.Ascending))
    expect(1) {
      mps.length
    }
    expect(2) {
      mps.head.id
    }
  }  
}