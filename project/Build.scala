import sbt._

object DevSearchScore extends Build {

  lazy val root = Project("root", file("."))
    //.dependsOn(astParser % "compile->compile;test->test")

  //add dependencies as follows...
  lazy val astCommit = "66821dfc27d217bd9f4828cd3701ffa2bf1f295a"
  lazy val astParser = RootProject(uri(s"git://github.com/devsearch-epfl/devsearch-ast.git#$astCommit"))
}

