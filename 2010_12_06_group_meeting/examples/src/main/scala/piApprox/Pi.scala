package piApprox

import scala.actors.Channel
import scala.actors.OutputChannel
import scala.actors.Actor
import scala.actors.Actor._
import scala.math._

/** Simple example of a split/merge scheme.
 *  Translated from a MPI example found at:
 *  http://www-unix.mcs.anl.gov/mpi/usingmpi/examples/simplempi/main.htm
 */

abstract class Msg
case class Intervals(i:Int) extends Msg
case class Sum(d:Double) extends Msg
case object Stop extends Msg

object Main extends Channel[Symbol] {

  def main(args: Array[String]) : Unit = {
    val nb = 100
    val ws = for { i <- List.range(1,1+nb) } yield { new Worker(i,nb) }
    ws.foreach(w => w.start)
    new Master(this, ws).start
    ?
  }

}

class Master(done: OutputChannel[Symbol], workers: List[Worker]) extends Actor {
  val s = workers.size
  
  def act() {
	val n = 200

	workers.foreach( w => w ! Intervals(n) )

	var pi = 0.0
	workers.foreach( w => receive {
	  case Sum(p) => pi += p
	  case err =>
	    println("error!!")
		error(err.toString)
	})

	println("PI ~= "+pi)
	println("error = "+ abs(pi - Pi))

	workers.foreach( w => w ! Stop)
    done ! 'done
	exit('stop)
  }
}

class Worker(id: Int, nbWorkers: Int) extends Actor {
  def act() {
    loop {
      react {
        case Intervals(n) => {
		  val h = 1.0 / n
		  val sum =
		    List.range(id,n+1,nbWorkers).
			map(i => {
			  val x = h * (i - 0.5)
			  4.0 / (1.0 + x*x)
			}).
			reduceLeft( (x,y) => x+y )
		  reply(Sum(h * sum))
		}
        case Stop =>
          //println("worker"+id+": Stop.")
          exit('stop)
      }
    }
  }
}
