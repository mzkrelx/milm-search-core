package org.milmsearch.core.api
import org.scalatest.FunSuite
import org.milmsearch.core.domain.{MLProposalSortBy => MLPSortBy}
import org.milmsearch.core.domain.SortOrder

class ResourceHelperSuite extends FunSuite {
  test("createSort ソート列名と値を指定した場合") {
    val sort = ResourceHelper.createSort[MLPSortBy.type](
      sortBy    = Some("createdAt"),
      sortOrder = Some("ascending"),
      toColumn  = MLPSortBy.withName(_))

    expect(true)(sort isDefined)
    expect(MLPSortBy.CreatedAt)(sort.get.column)
    expect(SortOrder.Ascending)(sort.get.sortOrder)
  }

  test("createSort ソート列名を指定して、ソート順序を指定しなかった場合") {
    val e = intercept[BadQueryParameterException] {
      ResourceHelper.createSort[MLPSortBy.type](
        sortBy    = Some("createdAt"),
        sortOrder = None,
        toColumn  = MLPSortBy.withName(_))
    }

    expect(true)(e.getMessage().startsWith("Invalid sort."))
  }

  test("createSort ソート列名を指定しないで、ソート順序を指定した場合") {
    val e = intercept[BadQueryParameterException] {
      ResourceHelper.createSort[MLPSortBy.type](
        sortBy    = None,
        sortOrder = Some("ascending"),
        toColumn  = MLPSortBy.withName(_))
    }

    expect(true)(e.getMessage().startsWith("Invalid sort."))
  }

  test("createSort ソート列名が規定外の場合") {
    val e = intercept[BadQueryParameterException] {
      ResourceHelper.createSort[MLPSortBy.type](
        sortBy    = Some("hello"),
        sortOrder = Some("ascending"),
        toColumn  = MLPSortBy.withName(_))
    }

    expect("Can't create sort. by[hello], order[ascending]")(
      e.getMessage())
  }

  test("createSort ソート列名とソート順序を指定しなかった場合") {
    val sort = ResourceHelper.createSort[MLPSortBy.type](
      sortBy    = None,
      sortOrder = None,
      toColumn  = MLPSortBy.withName(_))

    expect(None)(sort)
  }
}