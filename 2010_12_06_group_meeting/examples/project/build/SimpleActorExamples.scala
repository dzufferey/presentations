import sbt._

class SimpleActorExamples(info: ProjectInfo) extends DefaultProject(info)
{
    //override def mainClass = Some("piccolo.Main")
    override def artifactID = "sae"
    override def compileOptions = super.compileOptions ++ Seq(Unchecked)

    //dependencies
    val testInterface = "org.scala-tools.testing" % "test-interface" % "0.5"
    val scalatest = "org.scalatest" % "scalatest" % "1.2"

    // Additional Repositories
    val scalaToolsSnapshots = "Scala-Tools Maven2 Snapshots Repository" at "http://scala-tools.org/repo-snapshots"

}
