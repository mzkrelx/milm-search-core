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
name := "milm-search-core"

version := "0.1"

scalaVersion := "2.9.1"

artifactName := { (version: ScalaVersion, module: ModuleID, artifact: Artifact) =>
  artifact.name + "-" + module.revision + "." + artifact.extension
}

// for MilmSearch
libraryDependencies ++= Seq(
  "postgresql" % "postgresql" % "9.1-901.jdbc4",
  "javax.servlet" % "javax.servlet-api" % "3.0.1" % "provided",
  "org.apache.wink" % "wink-server" % "1.1.2-incubating",
  "org.slf4j" % "slf4j-log4j12" %  "1.6.6",
  "org.apache.commons" % "commons-lang3" % "3.1",
  "net.liftweb" %% "lift-mapper" % "2.4",
  "net.liftweb" %% "lift-json" % "2.4"
)

// for xsbt-web-plugin
seq(webSettings :_*)

libraryDependencies += "org.mortbay.jetty" % "jetty" % "6.1.22" % "container"

// test report for jenkins (output to target/test-reports/)
testListeners <<= target.map(t => Seq(new eu.henkelmann.sbt.JUnitXmlTestsListener(t.getAbsolutePath)))

// for test
libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "1.7.1" % "test",
  "org.scalamock" %% "scalamock-core" % "2.4",
  "org.scalamock" %% "scalamock-scalatest-support" % "2.4"
)

parallelExecution in Test := false

testOptions in Test += Tests.Setup { loader =>
  loader.loadClass("org.milmsearch.core.test.Boot").getMethod("setup").invoke(null)
}

testOptions in Test += Tests.Cleanup { loader =>
  loader.loadClass("org.milmsearch.core.test.Boot").getMethod("cleanup").invoke(null)
}

// for Scct (coverage report)
seq(ScctPlugin.instrumentSettings : _*)

parallelExecution in ScctTest := false

testOptions in ScctTest += Tests.Setup { loader =>
  loader.loadClass("org.milmsearch.core.test.Boot").getMethod("setup").invoke(null)
}

testOptions in ScctTest += Tests.Cleanup { loader =>
  loader.loadClass("org.milmsearch.core.test.Boot").getMethod("cleanup").invoke(null)
}

