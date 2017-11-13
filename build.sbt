name := """guanaco-web"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)


resolvers += Resolver.mavenLocal

scalaVersion := "2.11.11"

libraryDependencies += filters
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.0" % Test

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-stream-kafka" % "0.17",
  "com.typesafe.akka" %% "akka-http" % "10.0.7",
  "be.anova.guanaco" % "events" % "1.0-SNAPSHOT",
  "org.reactivemongo" %% "play2-reactivemongo" % "0.12.3",
  "org.webjars" %% "webjars-play" % "2.5.0-4",
  "org.webjars.bower" % "bootstrap-sass" % "3.3.7"
)

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.example.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.example.binders._"

(ElmKeys.elmOptions in ElmKeys.elmMake in Assets) ++= Seq("--debug")