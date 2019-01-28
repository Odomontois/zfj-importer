import sbtassembly.Plugin.AssemblyKeys
import AssemblyKeys._

assemblySettings

name := "zfj-importer"

version := "0.40"

scalaVersion := "2.10.4"

resolvers += "Atlassian Public" at "https://maven.atlassian.com/repository/public"

resolvers += "OSS" at "https://oss.sonatype.org/content/repositories/public"

resolvers += "Pentaho Repository" at "http://repository.pentaho.org/artifactory/repo/"

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"


libraryDependencies ++= Seq(
  "net.databinder" %% "dispatch-http" % "0.8.10",
  "net.databinder" %% "dispatch-mime" % "0.8.10",
  "net.debasishg" %% "sjson" % "0.19",
  "com.atlassian.jwt" % "jwt-plugin" % "1.6.1" withSources (),
  "com.atlassian.httpclient" % "atlassian-httpclient-api" % "0.21.1" withSources (),
  "com.atlassian.fugue" % "fugue" % "1.1" withSources (),
  "org.apache.poi" % "poi" % "3.9" withSources(),
  "org.apache.poi" % "poi-scratchpad" % "3.9",
  "org.apache.poi" % "poi-ooxml" % "3.9",
  "com.google.guava" % "guava" % "14.0.1",
  "commons-vfs" % "commons-vfs" % "2.0-20090205",
  "commons-configuration" % "commons-configuration" % "1.10",
  "org.apache.commons" % "commons-lang3" % "3.1",
  "commons-logging" % "commons-logging" % "1.1.1",
  "log4j" % "log4j" % "1.2.17",
  "org.scala-lang" % "scala-swing" % "2.10.2" withSources (),
  "org.specs2" %% "specs2-core" % "3.6.2" % "test"
)

scalacOptions in Test ++= Seq("-Yrangepos")

mainClass in assembly := Some("com.thed.zfj.ui.ImportSwingApp")

lazy val deploy = taskKey[Unit]("Deploys file")

deploy := {
  "./buildJar.sh " + version.value !
}

//excludedFiles in assembly := { (bases: Seq[File]) =>
//  bases flatMap { base =>
//    (base / "META-INF" * "*").get collect {
//      case f if f.getName == "NOTICE" => f
//      case f if f.getName == "NOTICE.txt" => f
//      case f if f.getName == "INDEX.LIST" => f
//      case f if f.getName.toLowerCase == "license" => f
//      case f if f.getName.toLowerCase == "license.txt" => f
//      case f if f.getName.toLowerCase == "manifest.mf" => f
//    }
//  }}

