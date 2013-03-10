/*
 * MilmSearch is a mailing list searching system.
 *
 * Copyright (C) 2013 MilmSearch Project.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 *
 * You can contact MilmSearch Project at mailing list
 * milm-search-public@lists.sourceforge.jp.
 */
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

  test("toAscOrDesc Ascending の場合") {
    expect(Ascending)(DaoHelper.toAscOrDesc(SortOrder.Ascending))
  }

  test("toAscOrDesc Descending の場合") {
    expect(Descending)(DaoHelper.toAscOrDesc(SortOrder.Descending))
  }

}