scalaVersion := "2.11.6"

name := """playflow"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava, PlayEbean)


libraryDependencies ++= Seq(
  javaCore,
  javaJdbc,
  cache,
  javaWs,
  javaJpa,
  "org.mockito" % "mockito-core" % "1.10.19"
)

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
// routesGenerator := InjectedRoutesGenerator

// Compile the project before generating Eclipse files, so that generated .scala or .class files for views and routes are present
EclipseKeys.preTasks := Seq(compile in Compile)