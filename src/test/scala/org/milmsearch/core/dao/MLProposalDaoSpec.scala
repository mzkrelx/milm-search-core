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
package org.milmsearch.core.dao

import java.net.URL
import java.sql.Timestamp

import org.joda.time.DateTime
import org.milmsearch.core.domain.{CreateMLProposalRequest => CreateMLPRequest}
import org.milmsearch.core.domain.MLArchiveType
import org.milmsearch.core.domain.{MLProposalStatus => MLPStatus}
import org.milmsearch.core.service.DateTimeService
import org.milmsearch.core.test.util.MockCreatable
import org.milmsearch.core.ComponentRegistry
import org.scalamock.scalatest.MockFactory
import org.scalamock.ProxyMockFactory
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.BeforeAndAfter
import org.scalatest.FunSpec

import mapper.MLProposalMetaMapper
import net.liftweb.mapper.DB
import net.liftweb.mapper.Schemifier

/**
 * MLProposalDao の単体テスト
 */
class MLProposalDaoSpec extends FunSpec with ShouldMatchers
    with BeforeAndAfter with ProxyMockFactory
    with MockFactory with MockCreatable {

  before {
    DB.runUpdate("DROP TABLE IF EXISTS ml_proposal", Nil)
    Schemifier.schemify(true, Schemifier.infoF _, MLProposalMetaMapper)
  }

  describe("create(request) メソッドは") {
    it("作成要求に全ての値が存在し、かつ正常値の場合は、DBにそのレコードを作成する") {
      val now = new DateTime(1986, 11, 28, 10, 12, 13, 0)
      val id = ComponentRegistry.dateTimeService.doWith(
        createMock[DateTimeService] {
		  _ expects 'now returning now
        }) {
          new MLProposalDaoImpl().create(
            CreateMLPRequest(
              "テスト 太郎",
              "proposer@example.com",
              "MLタイトル",
              MLPStatus.New,
              Some(MLArchiveType.Mailman),
              Some(new URL("http://localhost/path/to/archive/")),
              Some("コメント(MLの説明など)\nほげほげ)")))
        }

      id should equal (1)

      val (colNames, records) = DB.performQuery("SELECT * FROM ml_proposal")
      colNames should have length 12
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
      r("judged_at")      should equal (null)
      r("admin_comment")   should equal (null)
    }

    it("""作成要求に必要最低限の値が存在した場合は、任意項目は null の状態で
       |  DBにレコードを作成する""".stripMargin) {
      val now = new DateTime(1986, 11, 28, 10, 12, 13, 0)
      val id = ComponentRegistry.dateTimeService.doWith(
        createMock[DateTimeService] {
		  _ expects 'now returning now
        }) {
          new MLProposalDaoImpl().create(
            CreateMLPRequest(
              "テスト 太郎",
              "proposer@example.com",
              "MLタイトル",
              MLPStatus.New,
              archiveType = None,
              archiveURL  = None,
              comment     = None))
        }

      id should equal (1)

      val (colNames, records) = DB.performQuery("SELECT * FROM ml_proposal")
      colNames should have length 12
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
      r("judged_at")      should equal (null)
      r("admin_comment")   should equal (null)
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