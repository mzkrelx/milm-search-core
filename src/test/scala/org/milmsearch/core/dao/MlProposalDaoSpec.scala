package org.milmsearch.core.dao

import java.net.URL
import java.sql.Timestamp

import org.joda.time.DateTime
import org.milmsearch.core.domain.{CreateMlProposalRequest => CreateMLPRequest}
import org.milmsearch.core.domain.MlArchiveType
import org.milmsearch.core.domain.{MlProposalStatus => MLPStatus}
import org.milmsearch.core.service.DateTimeService
import org.milmsearch.core.test.util.MockCreatable
import org.milmsearch.core.ComponentRegistry
import org.scalamock.scalatest.MockFactory
import org.scalamock.ProxyMockFactory
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.BeforeAndAfter
import org.scalatest.FunSpec

import mapper.MlProposalMetaMapper
import net.liftweb.mapper.DB
import net.liftweb.mapper.Schemifier

/**
 * MlProposalDao の単体テスト
 */
class MlProposalDaoSpec extends FunSpec with ShouldMatchers
    with BeforeAndAfter with ProxyMockFactory
    with MockFactory with MockCreatable {

  before {
    DB.runUpdate("DROP TABLE IF EXISTS ml_proposal", Nil)
    Schemifier.schemify(true, Schemifier.infoF _, MlProposalMetaMapper)
  }

  describe("create(request) メソッドは") {
    it("作成要求に全ての値が存在し、かつ正常値の場合は、DBにそのレコードを作成する") {
      val now = new DateTime(1986, 11, 28, 10, 12, 13, 0)
      val id = ComponentRegistry.dateTimeService.doWith(
        createMock[DateTimeService] {
		  _ expects 'now returning now
        }) {
          new MlProposalDaoImpl().create(
            CreateMLPRequest(
              "テスト 太郎",
              "proposer@example.com",
              "MLタイトル",
              MLPStatus.New,
              Some(MlArchiveType.Mailman),
              Some(new URL("http://localhost/path/to/archive/")),
              Some("コメント(MLの説明など)\nほげほげ)")))
        }

      id should equal (1)

      val (colNames, records) = DB.performQuery("SELECT * FROM ml_proposal")
      colNames should have length 10
      records  should have length 1

      val r = recordToMap(colNames, records.head)
      r("id")             should equal (1)
      r("proposer_name")  should equal ("テスト 太郎")
      r("proposer_email") should equal ("proposer@example.com")
      r("ml_title")       should equal ("MLタイトル")
      r("status")         should equal ("new")
      r("archive_type")   should equal ("mailman")
      r("archive_url")    should equal ("http://localhost/path/to/archive/")
      r("message")        should equal ("コメント(MLの説明など)\nほげほげ)")
      r("created_at")     should equal (new Timestamp(now.getMillis))
      r("updated_at")     should equal (new Timestamp(now.getMillis))
    }

    it("""作成要求に必要最低限の値が存在した場合は、任意項目は null の状態で
       |  DBにレコードを作成する""".stripMargin) {
      val now = new DateTime(1986, 11, 28, 10, 12, 13, 0)
      val id = ComponentRegistry.dateTimeService.doWith(
        createMock[DateTimeService] {
		  _ expects 'now returning now
        }) {
          new MlProposalDaoImpl().create(
            CreateMLPRequest(
              "テスト 太郎",
              "proposer@example.com",
              "MLタイトル",
              MLPStatus.New,
              archiveType = None,
              archiveUrl  = None,
              comment     = None))
        }

      id should equal (1)

      val (colNames, records) = DB.performQuery("SELECT * FROM ml_proposal")
      colNames should have length 10
      records  should have length 1

      val r = recordToMap(colNames, records.head)
      r("id")             should equal (1)
      r("proposer_name")  should equal ("テスト 太郎")
      r("proposer_email") should equal ("proposer@example.com")
      r("ml_title")       should equal ("MLタイトル")
      r("status")         should equal ("new")
      r("archive_type")   should equal (null)
      r("archive_url")    should equal (null)
      r("message")        should equal (null)
      r("created_at")     should equal (new Timestamp(now.getMillis))
      r("updated_at")     should equal (new Timestamp(now.getMillis))
    }
  }

  /**
   * SQL 検索クエリの結果セットの一レコード分を [カラム名, 値] の Map にする
   *
   * @param カラム名の一覧
   * @param レコード
   * @return Map 形式のレコード
   */
  private def recordToMap(colNames: List[String], record: List[Any]) =
    Map[String, Any]() ++ colNames.map(_.toLowerCase).zip(record)
}