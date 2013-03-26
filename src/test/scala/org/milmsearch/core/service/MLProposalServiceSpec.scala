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
package org.milmsearch.core.service
import java.net.URL
import org.milmsearch.core.dao.MLProposalDao
import org.milmsearch.core.domain.MLArchiveType
import org.milmsearch.core.domain.{CreateMLProposalRequest => CreateMLPRequest}
import org.milmsearch.core.domain.MLProposalStatus
import org.milmsearch.core.ComponentRegistry
import org.scalamock.scalatest.MockFactory
import org.scalamock.ProxyMockFactory
import org.scalatest.FunSpec
import org.milmsearch.core.test.util.MockCreatable
import org.scalatest.matchers.ShouldMatchers

/**
 * MLProposalService の単体テスト
 */
class MLProposalServiceSpec extends FunSpec
    with MockFactory with ProxyMockFactory
    with MockCreatable with ShouldMatchers {

  describe("create(request) メソッドは") {
    it("""受け取った作成要求オブジェクトをそのまま MLProposalDao#create に渡し、
       |  DAO から受け取った レコードID をそのまま返す""".stripMargin) {
      val request = CreateMLPRequest(
        "申請者の名前",
        "proposer@example.com",
        "MLタイトル",
        MLProposalStatus.New,
        Some(MLArchiveType.Mailman),
        Some(new URL("http://localhost/path/to/archive/")),
        Some("コメント(MLの説明など)\nほげほげ)")
      )

      val id = ComponentRegistry.mlProposalDao.doWith(
          createMock[MLProposalDao] {
            _ expects 'create withArgs(request) returning 1L
        }) {
          new MLProposalServiceImpl().create(request)
        }

      id should equal (1L)
    }
  }

}