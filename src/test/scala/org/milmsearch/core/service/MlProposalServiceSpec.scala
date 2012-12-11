package org.milmsearch.core.service
import java.net.URL
import org.milmsearch.core.dao.MlProposalDao
import org.milmsearch.core.domain.MlArchiveType
import org.milmsearch.core.domain.{CreateMlProposalRequest => CreateMLPRequest}
import org.milmsearch.core.domain.MlProposalStatus
import org.milmsearch.core.ComponentRegistry
import org.scalamock.scalatest.MockFactory
import org.scalamock.ProxyMockFactory
import org.scalatest.FunSpec
import org.milmsearch.core.test.util.MockCreatable
import org.scalatest.matchers.ShouldMatchers

/**
 * MlProposalService の単体テスト
 */
class MlProposalServiceSpec extends FunSpec
    with MockFactory with ProxyMockFactory
    with MockCreatable with ShouldMatchers {

  describe("create(request) メソッドは") {
    it("""受け取った作成要求オブジェクトをそのまま MlProposalDao#create に渡し、
       |  DAO から受け取った レコードID をそのまま返す""".stripMargin) {
      val request = CreateMLPRequest(
        "申請者の名前",
        "proposer@example.com",
        "MLタイトル",
        MlProposalStatus.New,
        Some(MlArchiveType.Mailman),
        Some(new URL("http://localhost/path/to/archive/")),
        Some("コメント(MLの説明など)\nほげほげ)")
      )

      val id = ComponentRegistry.mlProposalDao.doWith(
          createMock[MlProposalDao] {
            _ expects 'create withArgs(request) returning 1L
        }) {
          new MlProposalServiceImpl().create(request)
        }
      
      id should equal (1L)
    }
  }

}