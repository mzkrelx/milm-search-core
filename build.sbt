name := "milm-search"

version := "0.0.1"

scalaVersion := "2.9.1"

seq(webSettings :_*)

libraryDependencies ++= Seq(
  "javax.servlet" % "servlet-api" % "2.5" % "provided",
  "org.mortbay.jetty" % "jetty" % "6.1.22" % "container",
  "org.apache.wink" % "wink-server" % "1.1.2-incubating",
  "org.apache.wink" % "wink" % "1.1.2-incubating",
  "org.apache.wink" % "wink-client" % "1.1.2-incubating",
  "dom4j" % "dom4j" % "1.6.1",
  "commons-logging" % "commons-logging" % "1.1.1",
  "org.apache.lucene" % "lucene-analyzers" % "2.9.4",
  "org.apache.lucene" % "lucene-highlighter" % "2.9.4",
  "nekohtml" % "nekohtml" % "1.9.6.2",
  "org.scalatest" %% "scalatest" % "1.7.1" % "test"
)

