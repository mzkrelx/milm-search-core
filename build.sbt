name := "milm-search"

version := "0.0.1"

scalaVersion := "2.9.1"

// for MilmSearch
libraryDependencies ++= Seq(
  "postgresql" % "postgresql" % "9.1-901.jdbc4",
  "javax.servlet" % "servlet-api" % "2.5" % "provided",
  "org.apache.wink" % "wink-server" % "1.1.2-incubating",
  "org.apache.wink" % "wink" % "1.1.2-incubating",
  "org.apache.wink" % "wink-client" % "1.1.2-incubating",
  "dom4j" % "dom4j" % "1.6.1",
  "commons-logging" % "commons-logging" % "1.1.1",
  "org.slf4j" % "slf4j-api" %  "1.6.6",
  "org.slf4j" % "slf4j-log4j12" %  "1.6.6",
  "org.apache.lucene" % "lucene-analyzers" % "2.9.4",
  "org.apache.lucene" % "lucene-highlighter" % "2.9.4",
  "nekohtml" % "nekohtml" % "1.9.6.2",
  "net.liftweb" %% "lift-mapper" % "2.4",
  "net.liftweb" %% "lift-json" % "2.4"
)

// for xsbt-web-plugin
seq(webSettings :_*)

libraryDependencies += "org.mortbay.jetty" % "jetty" % "6.1.22" % "container"

// for test
libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "1.7.1" % "test",
  "org.scalamock" %% "scalamock-core" % "2.4",
  "org.scalamock" %% "scalamock-scalatest-support" % "2.4"
)

testOptions in Test += Tests.Setup { loader =>
  loader.loadClass("org.milmsearch.core.test.Boot").getMethod("setup").invoke(null)
}

testOptions in Test += Tests.Cleanup { loader =>
  loader.loadClass("org.milmsearch.core.test.Boot").getMethod("cleanup").invoke(null)
}

