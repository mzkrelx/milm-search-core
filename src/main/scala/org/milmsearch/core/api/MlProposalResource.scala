package org.milmsearch.core.api
import javax.ws.rs.Path
import javax.ws.rs.POST
import javax.ws.rs.GET
import javax.ws.rs.core.Response
import javax.ws.rs.PUT
import javax.ws.rs.DELETE

@Path("/ml-proposals")
class MlProposalResource {
  
  @POST
  def create() = {
    Response.serverError().build()
  }

  @GET
  def list() = {
    Response.serverError().build()
  }

  @Path("{id}")
  @GET
  def show() = {
    Response.serverError().build()
  }

  @Path("{id}")
  @PUT
  def update() = {
    Response.serverError().build()
  }

  @Path("{id}")
  @DELETE
  def delete() = {
    Response.serverError().build()
  }
}
