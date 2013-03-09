package org.milmsearch.core.service
import java.net.URL
import org.milmsearch.core.dao.MLProposalDao
import org.milmsearch.core.domain.MlArchiveType
import org.milmsearch.core.domain.ML
import org.milmsearch.core.domain.{CreateMLProposalRequest => CreateMLPRequest}
import org.milmsearch.core.domain.MLProposalStatus
import org.milmsearch.core.{ComponentRegistry => CR}
import org.scalamock.scalatest.MockFactory
import org.scalamock.ProxyMockFactory
import org.scalatest.FeatureSpec
import org.milmsearch.core.test.util.MockCreatable
import org.scalatest.matchers.ShouldMatchers
import org.milmsearch.core.dao.MLDao
import java.util.Date
import org.joda.time.DateTime
import org.milmsearch.core.test.util.DateUtil._
import org.scalatest.GivenWhenThen

/**
 * MLService のテスト
 */
class MLServiceSpec extends FeatureSpec
    with MockFactory with ProxyMockFactory
    with MockCreatable with ShouldMatchers with GivenWhenThen {
  
  feature("MLService クラス") {
    
    scenario("存在するML情報を検索する") {
      given("存在するML情報の ID を引数に")
      val mlID = 1L
      val m = createMock[MLDao] {
        _ expects 'find withArgs(mlID) returning Some(newSampleML)
      }
      
      when("find メソッドを呼び出した時に")
      then("Some(検索したML情報) を返す")
      CR.mlDao.doWith(m) {
        new MLServiceImpl().find(mlID) should equal (Some(newSampleML))
      }
    }
    
    scenario("存在しないML情報を検索する") {
      given("存在しないML情報の ID を引数に")
      val mlID = 0L
      val m = createMock[MLDao] {
        _ expects 'find withArgs(mlID) returning None
      }
      
      when("find メソッドを呼び出した時に")
      then("None を返す")
      CR.mlDao.doWith(m) {
        new MLServiceImpl().find(mlID) should equal (None)
      }
    }
    
  }
  
  /**
   * サンプルML情報を生成する
   */
  private def newSampleML = ML(
    id           = 1L,
    title        = "ML タイトル",
    archiveType  = MlArchiveType.Mailman,
    archiveURL   = new URL("http://localhost/path/to/archive/"),
    lastMailedAt = newDateTime(2013, 1, 1),
    approvedAt   = newDateTime(2013, 1, 5))
}