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

import org.milmsearch.core.domain.ML
import org.milmsearch.core.domain.MLArchiveType
import org.milmsearch.core.test.util.DateUtil.newDate
import org.milmsearch.core.test.util.DateUtil.newDateTime
import org.milmsearch.core.test.util.MockCreatable
import org.scalamock.scalatest.MockFactory
import org.scalamock.ProxyMockFactory
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.BeforeAndAfter
import org.scalatest.FeatureSpec
import org.scalatest.GivenWhenThen

import mapper.MLMetaMapper
import net.liftweb.mapper.DB
import net.liftweb.mapper.Schemifier

/**
 * MLDao のテスト
 */
class MLDaoSpec extends FeatureSpec
    with MockFactory with ProxyMockFactory
    with MockCreatable with ShouldMatchers
    with GivenWhenThen with BeforeAndAfter {

  before {
    DB.runUpdate("DROP TABLE IF EXISTS ml", Nil)
    Schemifier.schemify(true, Schemifier.infoF _, MLMetaMapper)
  }

  feature("MLDao クラス") {

    scenario("存在するML情報を検索する") {
      given("存在するML情報の ID を引数に")
      insertSampleML1()
      val mlID = 1L

      when("find メソッドを呼び出した時に")
      then("Some(検索したML情報) を返す")
      new MLDaoImpl().find(mlID) should equal (Some(newSampleML))
    }

    scenario("最終投稿日時がnullのML情報を検索する") {
      given("存在するML情報の ID を引数に")
      insertSampleML2()
      val mlID = 1L

      when("find メソッドを呼び出した時に")
      then("Some(検索したML情報) を返す")
      new MLDaoImpl().find(mlID) should equal (Some(newSampleML2))
    }

    scenario("存在しないML情報を検索する") {
      given("存在しないML情報の ID を引数に")
      insertSampleML1()
      val mlID = 2L

      when("find メソッドを呼び出した時に")
      then("None を返す")
      new MLDaoImpl().find(mlID) should equal (None)
    }

  }

  /**
   * サンプルML情報を DB に INSERT する
   */
  private def insertSampleML1() {
    DB.runUpdate("""
      |INSERT INTO ml
      |  (id, title, archive_type, archive_url, last_mailed_at, approved_at)
      |  VALUES(?,?,?,?,?,?)""".stripMargin.stripLineEnd,
      List(1L, "ML タイトル", "mailman", "http://localhost/path/to/archive/",
        newDate(2013, 1, 1), newDate(2013, 1, 5)))
  }

  /**
   * サンプルML情報を DB に INSERT する
   *
   * 最終投稿日時がnull
   */
  private def insertSampleML2() {
    DB.runUpdate("""
      |INSERT INTO ml
      |  (id, title, archive_type, archive_url, last_mailed_at, approved_at)
      |  VALUES(?,?,?,?,?,?)""".stripMargin.stripLineEnd,
      List(1L, "ML タイトル", "mailman", "http://localhost/path/to/archive/",
        null, newDate(2013, 1, 5)))
  }

  /**
   * サンプルML情報を生成する
   */
  private def newSampleML = ML(
    id           = 1L,
    title        = "ML タイトル",
    archiveType  = MLArchiveType.Mailman,
    archiveURL   = new URL("http://localhost/path/to/archive/"),
    lastMailedAt = Some(newDateTime(2013, 1, 1)),
    approvedAt   = newDateTime(2013, 1, 5))

  /**
   * サンプルML情報を生成する
   *
   * 最終投稿日時がnull
   */
  private def newSampleML2 = ML(
    id           = 1L,
    title        = "ML タイトル",
    archiveType  = MLArchiveType.Mailman,
    archiveURL   = new URL("http://localhost/path/to/archive/"),
    lastMailedAt = None,
    approvedAt   = newDateTime(2013, 1, 5))

}