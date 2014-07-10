
addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.10.0")

addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse-plugin" % "2.5.0")
//addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.0.0")

resolvers += Resolver.url("sbt-plugin-releases",
  new URL("http://scalasbt.artifactoryonline.com/scalasbt/sbt-plugin-releases/"))(Resolver.ivyStylePatterns)

resolvers += "sbt-idea-repo" at "http://mpeltonen.github.com/maven/"
