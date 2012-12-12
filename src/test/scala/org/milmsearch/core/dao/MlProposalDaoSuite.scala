package org.milmsearch.core.dao
import java.net.URL
import org.milmsearch.core.domain.CreateMlProposalRequest
import org.milmsearch.core.domain.MlArchiveType
import org.milmsearch.core.domain.MlProposalStatus
import org.scalatest.BeforeAndAfterAll
import org.scalatest.BeforeAndAfter
import org.scalatest.FunSuite
import net.liftweb.db.DB1.db1ToDb
import net.liftweb.mapper.DB
import net.liftweb.mapper.Schemifier
import mapper.{ MlProposalMetaMapper => MLPMMAPPER }

class MlProposalDaoSuite extends FunSuite with BeforeAndAfterAll with BeforeAndAfter {
  override def beforeAll {
    Schemifier.schemify(true, Schemifier.infoF _,
      MLPMMAPPER)
  }

  after {
    DB.runUpdate("TRUNCATE TABLE ML_PROPOSAL", Nil)
  }

  test("insert full") { pending }

  test("update full") {
    val request = CreateMlProposalRequest(
      "changedName",
      "proposer@example.com",
      "MLタイトル",
      MlProposalStatus.New,
      Some(MlArchiveType.Mailman),
      Some(new URL("http://localhost/path/to/archive/")),
      Some("コメント(MLの説明など)"))

    val id = 1L
    DB.runUpdate("INSERT INTO ml_proposal VALUES(?,?,?,?,?,?,?,?,?,?)",
      List(1, "name1", "sample1@sample.com", "title1", 2, 1,
        "http://sample.com2", "message2", "2012-10-10 10:10:11", "2012-10-11 10:10:11"))

    expect(true) {
      new MlProposalDaoImpl().update(1, request)
    }

    val (column, rows) = DB.runQuery("SELECT * FROM ml_proposal WHERE id = 1")

    expect(1) {
	  rows.head.head.toInt
    }

//    expect("changedName") {
//      rows.head.proposerName
//    }


  }

  test("update empty") { pending }

  test("update failure") { pending }
}