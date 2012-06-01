import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "Jira2Gantt"
    val appVersion      = "1.0"

    val appDependencies = Seq(
      "org.jasypt" % "jasypt" % "1.9.0"
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
      // Add your own project settings here      
    )

}
