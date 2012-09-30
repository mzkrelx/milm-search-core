package org.milmsearch.core.api

import javax.ws.rs.core.Application
import scala.collection.JavaConverters._

class MilmSearchApiApplication extends Application {
  override def getClasses(): java.util.Set[Class[_]] = {
    Set[Class[_]](
        classOf[MlProposalResource],
        classOf[SampleListResource]
    ).asJava
  }
}
