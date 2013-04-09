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
package org.milmsearch.core
import dao._
import service._
import net.liftweb.util.SimpleInjector

/**
 * サービスロケーター<br/
 * Service と DAO の初期化を行う
 */
object ComponentRegistry extends SimpleInjector {
  val mlProposalDao     = new Inject[MLProposalDao](new MLProposalDaoImpl) {}
  val mlProposalService = new Inject[MLProposalService](new MLProposalServiceImpl) {}

  val mlDao     = new Inject[MLDao](new MLDaoImpl) {}
  val mlService = new Inject[MLService](new MLServiceImpl) {}

  val dateTimeService = new Inject[DateTimeService](new DateTimeServiceImpl) {}
}