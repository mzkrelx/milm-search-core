package org.milmsearch.core.api
import javax.ws.rs.core.Response
import net.liftweb.common.Loggable


object ResourceHelper extends Loggable {
  def getLongParam(param: String): Option[Long] =
    param match {
      case null => None
      case p =>
        try {
          Some(p.toLong)
        } catch {
            case e: NumberFormatException => throw new BadQueryParameterException(
              "[%s] is not numeric." format (param))
        }
    }

  def getBooleanParam(param: String): Option[Boolean] =
    param match {
      case null => None
      case p =>
        try {
          Some(p.toBoolean)
        } catch {
            case e: NumberFormatException => throw new BadQueryParameterException(
              "[%s] is not boolean." format (param))
        }
    }

  def ok(body: String) = Response.ok(body).build()

  def err400(msg: String): Response = {
    logger.error(msg)
    Response.status(Response.Status.BAD_REQUEST).build()
  }

  def err404(msg: String): Response = {
    logger.error(msg)
    Response.status(Response.Status.NOT_FOUND).build()
  }

}