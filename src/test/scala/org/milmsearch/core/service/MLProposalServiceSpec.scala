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