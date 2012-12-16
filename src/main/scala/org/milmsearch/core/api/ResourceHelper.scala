package org.milmsearch.core.api

object ResourceHelper {
  def getLongParam(param: String, paramName: String): Option[Long] =
    param match {
      case null => None
      case numeric => 
        try {
          Some(numeric.toLong)
        } catch {
            case e: NumberFormatException => throw new BadQueryParameterException(
              "Invalid [%s] value. [%s]" format (paramName, param))
        }
    }
}