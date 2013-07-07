name := "scala-benchmar"

version := "0.1"

scalaVersion := "2.10.1"

libraryDependencies ++=
  Seq(
    "junit" % "junit" % "4.11" % "test",
    "org.hamcrest" % "hamcrest-core" % "1.3" % "test",
    "com.novocode" % "junit-interface" % "0.8" % "test->default"
  )
