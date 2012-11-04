package org.milmsearch.core.dao
import org.milmsearch.core.domain.MlProposalStatus
import org.milmsearch.core.domain.Range
import org.milmsearch.core.domain.Sort
import org.milmsearch.core.domain.SortOrder
import org.milmsearch.core.Bootstrap
import org.scalatest.BeforeAndAfterAll
import org.scalatest.FunSuite
import net.liftweb.mapper.By
import net.liftweb.mapper.DB
import net.liftweb.mapper.Schemifier
import org.milmsearch.core.domain.Filter
import org.milmsearch.core.domain.MlProposalFilterBy

class MlProposalDaoSuite extends FunSuite with BeforeAndAfterAll {
  // TODO
  test("insert full") { pending }
  
  /**
   * 全てのテストの前処理
   */
  override def beforeAll() {
    Schemifier.schemify(true, Schemifier.infoF _,
      mapper.MlProposalMetaMapper
    )
  }
  
  test("toMappedField id to id") {
    val mpDao = new MlProposalDaoImpl()
    expect(mapper.MlProposalMetaMapper.id) {
      mpDao.toMappedField(mapper.MlProposalField.Id)
    }
  }
  
  test("toMappedField proposerName to proposerName") {
    val mpDao = new MlProposalDaoImpl()
    expect(mapper.MlProposalMetaMapper.proposerName)(mpDao.toMappedField(mapper.MlProposalField.ProposerName))
  }
  
  test("toMappedField proposerEmail to proposerEmail") {
    val mpDao = new MlProposalDaoImpl()
    expect(mapper.MlProposalMetaMapper.proposerEmail)(mpDao.toMappedField(mapper.MlProposalField.ProposerEmail))
  }
  
  test("toMappedField mlTitle to mlTitle") {
    val mpDao = new MlProposalDaoImpl()
    expect(mapper.MlProposalMetaMapper.mlTitle)(mpDao.toMappedField(mapper.MlProposalField.MlTitle))
  }
  
  test("toMappedField status to status") {
    val mpDao = new MlProposalDaoImpl()
    expect(mapper.MlProposalMetaMapper.status)(mpDao.toMappedField(mapper.MlProposalField.Status))
  }

  test("toMappedField archiveType to archiveType") {
    val mpDao = new MlProposalDaoImpl()
    expect(mapper.MlProposalMetaMapper.archiveType)(mpDao.toMappedField(mapper.MlProposalField.ArchiveType))
  }

  test("toMappedField archiveUrl to archiveUrl") {
    val mpDao = new MlProposalDaoImpl()
    expect(mapper.MlProposalMetaMapper.archiveUrl)(mpDao.toMappedField(mapper.MlProposalField.ArchiveUrl))
  }

  test("toMappedField comment to message") {
    val mpDao = new MlProposalDaoImpl()
    expect(mapper.MlProposalMetaMapper.message)(mpDao.toMappedField(mapper.MlProposalField.Comment))
  }

  test("toMappedField createdAt to createdAt") {
    val mpDao = new MlProposalDaoImpl()
    expect(mapper.MlProposalMetaMapper.createdAt)(mpDao.toMappedField(mapper.MlProposalField.CreatedAt))
  }

  test("toMappedField updatedAt to updatedAt") {
    val mpDao = new MlProposalDaoImpl()
    expect(mapper.MlProposalMetaMapper.updatedAt)(mpDao.toMappedField(mapper.MlProposalField.UpdatedAt))
  }
  
  test("toBy") {
    val mpDao = new MlProposalDaoImpl()
    expect(
      By(mapper.MlProposalMetaMapper.status, MlProposalStatus.Accepted)
    ) (mpDao.toBy(
      Filter(MlProposalFilterBy.Status, MlProposalStatus.Accepted))
    )
  }
  
  test("findAll") {
    DB.runUpdate("INSERT INTO ML_PROPOSAL VALUES(?,?,?,?,?,?,?,?,?,?)", 
        List(1, "name1", "sample@sample.com", "title", 
          1, 1, "http://sample.com", "message",
          "2012-10-10 10:10:11", "2012-10-11 10:10:11"
        )
    )
    val mpDao = new MlProposalDaoImpl()
    val mps = mpDao.findAll(Range(0, 10), Sort('id, SortOrder.Ascending))
    println("!!!!" + mps)
    val mp = mps.head
    expect(1)(mp.id)
  }
  
}