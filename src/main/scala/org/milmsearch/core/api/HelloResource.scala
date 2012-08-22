package org.milmsearch.core.api

import javax.ws.rs.core.Response
import javax.ws.rs.GET
import javax.ws.rs.Path

@Path("/")
class HelloResource {
  @GET
  def hello(): Response = {
    val res =
      <html xmlns="http://www.w3.org/1999/xhtml">
        <body>Hello, World</body>
      </html>
    Response.ok(res.toString()).build()
  }
}