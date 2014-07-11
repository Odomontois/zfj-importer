import AssemblyKeys._

assemblySettings

name := "zfj-importer "

version := "0.29"

scalaVersion := "2.10.2"

//resolvers += "Local Maven Repository" at "file:///Users/smangal/.m2/repository"

resolvers += "Pentaho Repository" at "http://repository.pentaho.org/artifactory/repo/"

libraryDependencies ++= Seq(
  "net.databinder" %% "dispatch-http" % "0.8.+",
  "net.databinder" %% "dispatch-mime" % "0.8.+",
  "net.debasishg" %% "sjson" % "0.19",
  "org.apache.poi" % "poi" % "3.+" withSources(),
  "org.apache.poi" % "poi-scratchpad" % "3.+",
  "org.apache.poi" % "poi-ooxml" % "3.+",
  "commons-vfs" % "commons-vfs" % "2.0-20090205",
  "commons-lang" % "commons-lang" % "2.6",
  "commons-logging" % "commons-logging" % "1.1.1",
  "log4j" % "log4j" % "1.2.17",
  "org.scala-lang" % "scala-swing" % "2.10.2" withSources ()
)

mainClass in assembly := Some("com.thed.zfj.ui.ImportSwingApp")

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

