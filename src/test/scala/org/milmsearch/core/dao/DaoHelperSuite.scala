package org.milmsearch.core.dao
import org.milmsearch.core.Bootstrap
import org.milmsearch.core.ComponentRegistry
import org.scalatest.BeforeAndAfterAll
import org.scalatest.FunSuite
import net.liftweb.mapper.DB
import net.liftweb.mapper.Schemifier
import org.milmsearch.core.domain.Range
import org.milmsearch.core.domain.Sort
import org.milmsearch.core.domain.SortOrder
import net.liftweb.mapper.Ascending
import net.liftweb.mapper.Descending

class DaoHelperSuite extends FunSuite {
  
  test("toAscOrDesc of asc") {
    expect(Ascending)(DaoHelper.toAscOrDesc(SortOrder.Ascending))
  }
  
  test("toAscOrDesc of desc") {
    expect(Descending)(DaoHelper.toAscOrDesc(SortOrder.Descending))
  }
  
}