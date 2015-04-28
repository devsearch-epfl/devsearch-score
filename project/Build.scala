import sbt._

object DevSearchScore extends Build {

  lazy val root = Project("root", file("."))
    .dependsOn(devsearchLearning % "compile->compile;test->test")

  //add dependencies as follows...
  lazy val learningCommit = "aeee5a232e14d90e98961e74abe41aa8ce2f6649"
  lazy val devsearchLearning = RootProject(uri(s"git://github.com/devsearch-epfl/devsearch-learning.git#$learningCommit"))
}

