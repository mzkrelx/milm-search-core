package org.milmsearch.core.api
import org.scalatest.FunSuite
import org.apache.wink.client.Resource
import java.net.URL
import org.apache.wink.client.RestClient
import java.net.URI

class MlProposalResourceSuite extends FunSuite {

  test("create") {
    // TODO post body
    val response = makeResource("/ml-proposals").post("")

    expect(201) { response.getStatusCode }
    
    // TODO location header test
  }
  
  test("list") {
    val response = makeResource("/ml-proposals").get()

    expect(200) { response.getStatusCode }

    // TODO response body test
  }
  
  test("show") {
    // TODO resource id
    val response = makeResource("/ml-proposals/123").get()

    expect(200) { response.getStatusCode }

    // TODO response body test
  }

  test("update") {
    // TODO resource id
    // TODO put body
    val response = makeResource("/ml-proposals/123").put("")

    expect(200) { response.getStatusCode }
  }
  
  test("delete") {
    // TODO resource id
    val response = makeResource("/ml-proposals/123").delete()

    expect(200) { response.getStatusCode }
  }
  
  private def makeResource(path: String) =
    new RestClient().resource(
      new URI("http://localhost:8080/api" + path))
}