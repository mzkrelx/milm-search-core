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
import org.apache.commons.lang3.time.DateFormatUtils
import org.milmsearch.core.domain.CreateMLProposalRequest
import org.milmsearch.core.domain.Filter
import org.milmsearch.core.domain.MLArchiveType
import org.milmsearch.core.domain.{MLProposalFilterBy => MLPFilterBy}
import org.milmsearch.core.domain.{MLProposalSortBy => MLPSortBy}
import org.milmsearch.core.domain.{MLProposalStatus => MLPStatus}
import org.milmsearch.core.domain.MLProposalColumn
import org.milmsearch.core.domain.Range
import org.milmsearch.core.domain.Sort
import org.milmsearch.core.domain.SortOrder
import org.milmsearch.core.Bootstrap
import org.scalatest.BeforeAndAfter
import org.scalatest.BeforeAndAfter
import org.scalatest.BeforeAndAfterAll
import org.scalatest.FunSuite
import org.scalatest.FunSuite
import mapper.{MLProposalMetaMapper => MLPMMapper}
import mapper.MLProposalMetaMapper
import net.liftweb.db.DB1.db1ToDb
import net.liftweb.mapper.Ascending
import net.liftweb.mapper.By
import net.liftweb.mapper.DB
import net.liftweb.mapper.OrderBy
import net.liftweb.mapper.Schemifier
import org.milmsearch.core.test.util.DateUtil

class MLProposalDaoSuite extends FunSuite with BeforeAndAfterAll
    with BeforeAndAfter {

  private val insert1RecordSql = """
      | INSERT INTO ml_proposal (
        | id,
        | proposer_name,
        | proposer_email,
        | ml_title,
        | status,
        | archive_type,
        | archive_url,
        | message,
        | created_at,
        | updated_at,
        | judged_at
      | ) VALUES (?,?,?,?,?,?,?,?,?,?,?)""".stripMargin

  /**
   * 全てのテストの前処理
   */
  override def beforeAll() {
    Schemifier.schemify(true, Schemifier.infoF _, MLPMMapper)
  }

  after {
    DB.runUpdate("TRUNCATE TABLE ML_PROPOSAL", Nil)
  }

  test("toBy ステータスが承認済みを変換") {
    expect(By(MLPMMapper.status, MLPStatus.Accepted.toString)) {
      new MLProposalDaoImpl().toBy(
        Filter(MLPFilterBy.Status, MLPStatus.Accepted))
    }
  }

  test("toOrderBy 作成日時の昇順を変換") {
    expect(OrderBy(MLPMMapper.createdAt, Ascending)) {
      new MLProposalDaoImpl().toOrderBy(
        Sort(MLPSortBy.CreatedAt, SortOrder.Ascending))
    }
  }

  test("findAll 1件DBに入っていて、その1件が取得できるか") {

    DB.runUpdate(insert1RecordSql,
      List(1, "name1", "sample@sample.com", "title",
        "accepted", "other", "http://sample.com", "message",
        "2012-10-10 10:10:11", "2012-10-11 10:10:11", "2012-10-12 10:10:11"))

    val mps = new MLProposalDaoImpl().findAll(Range(0, 10))

    expect(1)(mps.length)

    val mp = mps.head

    expect(1)(mp.id)
    expect("name1")(mp.proposerName)
    expect("sample@sample.com")(mp.proposerEmail)
    expect("title")(mp.mlTitle)
    expect(MLPStatus.Accepted)(mp.status)
    expect(Some(MLArchiveType.Other))(mp.archiveType)
    expect(Some(new URL("http://sample.com")))(mp.archiveURL)
    expect(Some("message"))(mp.comment)
    expect("2012-10-10T10:10:11")(
      DateFormatUtils.ISO_DATETIME_FORMAT.format(mp.createdAt))
    expect("2012-10-11T10:10:11")(
      DateFormatUtils.ISO_DATETIME_FORMAT.format(mp.updatedAt))
    expect(Some(DateUtil.createDate("2012/10/12 10:10:11")))(
      mp.judgedAt)
  }

  test("findAll 検索条件にあうものが取得できるか") {
    DB.runUpdate(insert1RecordSql + """
      | ,(?,?,?,?,?,?,?,?,?,?,?)
      | ,(?,?,?,?,?,?,?,?,?,?,?)""".stripMargin,
      List(
        // 1 件目
        1, "name1", "sample@sample.com", "title",
        "new", "other", "http://sample.com", "message",
        "2012-10-10 15:10:11", "2012-10-11 10:10:11", "2012-10-12 10:10:11",
        // 2 件目
        2, "name2", "sample2@sample.com", "title2",
        "accepted", "other", "http://sample.com2", "message2",
        "2012-10-10 11:10:11", "2012-10-11 10:10:11", "2012-10-12 10:10:11",
        // 3 件目 検索対象・ステータスが Rejected(2)
        3, "name3", "sample3@sample.com", "title3",
        "rejected", "other", "http://sample.com3", "message3",
        "2012-10-10 12:10:11", "2012-10-11 10:10:11", "2012-10-12 10:10:11"))

    val mps = new MLProposalDaoImpl().findAll(
      Range(0, 10), None,
      Some(Filter(MLPFilterBy.Status, MLPStatus.Rejected)))

    expect(1)(mps.length)
    expect(3)(mps.head.id)
  }

  test("findAll 並び順が指定した通りになるか") {
    DB.runUpdate(insert1RecordSql + """
      | ,(?,?,?,?,?,?,?,?,?,?,?)
      | ,(?,?,?,?,?,?,?,?,?,?,?)""".stripMargin,
      List(
        // 1 件目
        1, "name1", "sample@sample.com", "title",
        "accepted", "other", "http://sample.com", "message",
        "2012-10-10 15:10:11", "2012-10-11 10:10:11", "2012-10-12 10:10:11",
        // 2 件目
        2, "name2", "sample2@sample.com", "title2",
        "rejected", "other", "http://sample.com2", "message2",
        "2012-10-10 11:10:11", "2012-10-11 10:10:11", "2012-10-12 10:10:11",
        // 3 件目
        3, "name3", "sample3@sample.com", "title3",
        "rejected", "other", "http://sample.com3", "message3",
        "2012-10-10 12:10:11", "2012-10-11 10:10:11", "2012-10-12 10:10:11"))

    val mps = new MLProposalDaoImpl().findAll(
      Range(0, 10),
      Some(Sort(MLPSortBy.CreatedAt, SortOrder.Descending)),
      None)

    // id でいうと 1, 3, 2 の 順番になる
    expect(3)(mps.length)
    expect(1)(mps.head.id)       // 1番目
    expect(3)(mps.tail.head.id)  // 2番目
  }

  test("findAll 取得範囲が指定した通りになるか") {
    DB.runUpdate(insert1RecordSql + """
      | ,(?,?,?,?,?,?,?,?,?,?,?)
      | ,(?,?,?,?,?,?,?,?,?,?,?)""".stripMargin,
      List(
        // 1 件目
        1, "name1", "sample@sample.com", "title",
        "accepted", "other", "http://sample.com", "message",
        "2012-10-10 10:10:11", "2012-10-11 10:10:11", "2012-10-12 10:10:11",
        // 2 件目
        2, "name2", "sample2@sample.com", "title2",
        "rejected", "other", "http://sample.com2", "message2",
        "2012-10-10 10:10:11", "2012-10-11 10:10:11", "2012-10-12 10:10:11",
        // 3 件目
        3, "name3", "sample3@sample.com", "title3",
        "rejected", "other", "http://sample.com3", "message3",
        "2012-10-10 10:10:11", "2012-10-11 10:10:11", "2012-10-12 10:10:11"))

    val mps = new MLProposalDaoImpl().findAll(Range(1, 1))

    expect(1)(mps.length)
  }

  test("delete_正常") {
    val id = 1L
    DB.runUpdate(insert1RecordSql,
        List(1, "name2", "sample2@sample.com", "title2", 2, 1,
            "http://sample.com2", "message2", "2012-10-10 10:10:11",
            "2012-10-11 10:10:11", "2012-10-12 10:10:11"
            )) // 一旦挿入して、（prepared statement）
    expect(true) {
      new MLProposalDaoImpl().delete(id) // それを削除する
    }
    expect(0) { // 削除結果を確認する
      val (columns, rows) = DB.runQuery("SELECT COUNT(id) FROM ml_proposal") //件数を取得するSQL
      rows.head.head.toInt // runQuery の戻り値は (List(COUNT(ID)),List(List(0)))
    }
  }

  test("delete_idなし") {
    val id = 1L
    expect(false) {
      new MLProposalDaoImpl().delete(id)
    }
    expect(0) {
      val (columns, rows) = DB.runQuery("SELECT COUNT(id) FROM ml_proposal") //件数を取得するSQL
      rows.head.head.toInt // runQuery の戻り値は (List(COUNT(ID)),List(List(0)))
    }
  }

  test("find 1件DBに入っていて、その1件が取得できるか") {
    DB.runUpdate(insert1RecordSql,
      List(1, "name1", "sample@sample.com", "title",
        "accepted", "other", "http://sample.com", "message",
        "2012-10-10 10:10:11", "2012-10-11 10:10:11", "2012-10-12 10:10:11"))

    val mlp = new MLProposalDaoImpl().find(1)

    expect(1)(mlp.get.id)
    expect("name1")(mlp.get.proposerName)
    expect("sample@sample.com")(mlp.get.proposerEmail)
    expect("title")(mlp.get.mlTitle)
    expect(MLPStatus.Accepted)(mlp.get.status)
    expect(Some(MLArchiveType.Other))(mlp.get.archiveType)
    expect(Some(new URL("http://sample.com")))(mlp.get.archiveURL)
    expect(Some("message"))(mlp.get.comment)
    expect("2012-10-10T10:10:11")(
      DateFormatUtils.ISO_DATETIME_FORMAT.format(mlp.get.createdAt))
    expect("2012-10-11T10:10:11")(
      DateFormatUtils.ISO_DATETIME_FORMAT.format(mlp.get.updatedAt))
    expect(Some(DateUtil.createDate("2012/10/12 10:10:11")))(
      mlp.get.judgedAt)
  }

  test("update(id, colVal) proposerNameを更新") {
    DB.runUpdate(insert1RecordSql,
      List(1, "name1", "sample@sample.com", "title",
        "accepted", "other", "http://sample.com", "message",
        "2012-10-10 10:10:11", "2012-10-11 10:10:11", "2012-10-12 10:10:11"))

    expect(true) {
      new MLProposalDaoImpl().update(
        1, Pair(MLProposalColumn.ProposerName, "hideo"))
    }

    val mlp = new MLProposalDaoImpl().find(1).get
    expect("hideo")(mlp.proposerName)
  }

  test("update(id, colVal) archiveTypeを更新") {
    DB.runUpdate(insert1RecordSql,
      List(1, "name1", "sample@sample.com", "title",
        "accepted", "other", "http://sample.com", "message",
        "2012-10-10 10:10:11", "2012-10-11 10:10:11", "2012-10-12 10:10:11"))

    expect(true) {
      new MLProposalDaoImpl().update(
        1, Pair(MLProposalColumn.ArchiveType, MLArchiveType.Mailman))
    }

    val mlp = new MLProposalDaoImpl().find(1).get
    expect("mailman")(mlp.archiveType.get.toString)
  }

  test("update(id, colVal) archiveURLを更新") {
    DB.runUpdate(insert1RecordSql,
      List(1, "name1", "sample@sample.com", "title",
        "accepted", "other", "http://sample.com", "message",
        "2012-10-10 10:10:11", "2012-10-11 10:10:11", "2012-10-12 10:10:11"))

    expect(true) {
      new MLProposalDaoImpl().update(
        1, Pair(MLProposalColumn.ArchiveURL, new URL("http://test.com")))
    }

    val mlp = new MLProposalDaoImpl().find(1).get
    expect("http://test.com")(mlp.archiveURL.get.toString)
  }

  test("update(id, colVal) 存在しないIDを指定") {
    expect(false) {
      new MLProposalDaoImpl().update(
        1, Pair(MLProposalColumn.ProposerName, "hideo"))
    }
  }

  test("update(id, colValList) proposerNameと更新日時を更新") {
    val judgedAt = DateUtil.createDate("2013/01/01 00:00:00")

    DB.runUpdate(insert1RecordSql,
      List(1, "name1", "sample@sample.com", "title",
        "accepted", "other", "http://sample.com", "message",
        "2012-10-10 10:10:11", "2012-10-11 10:10:11", "2012-10-12 10:10:11"))

    expect(true) {
      new MLProposalDaoImpl().update(
        1, List(
          (MLProposalColumn.ProposerName, "hideo"),
          (MLProposalColumn.JudgedAt, judgedAt)))
    }

    val mlp = new MLProposalDaoImpl().find(1)
    expect("hideo")(mlp.get.proposerName)
    expect("2013-01-01T00:00:00")(
      DateFormatUtils.ISO_DATETIME_FORMAT.format(mlp.get.judgedAt.get))
  }

}