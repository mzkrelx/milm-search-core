package org.milmsearch.core.test.util
import org.scalamock.Mock
import org.scalamock.ProxyMockFactory
import org.scalamock.scalatest.MockFactory

trait MockCreatable {

  protected def mock[T: ClassManifest]: T with Mock

  def createMock[T: ClassManifest](f: T with Mock => Unit): T = {
    val m = mock[T]
    f(m)
    m
  }
}