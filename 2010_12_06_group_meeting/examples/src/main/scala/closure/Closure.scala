package closure

import scala.actors.Channel
import scala.actors.OutputChannel
import scala.actors.Actor
import scala.actors.Actor._

object Main extends Channel[Symbol] {

  def main(args: Array[String]): Unit = {
    val scopes = new Scopes(this)
    scopes.start
    ?
    queue foreach Console.println
  }

  val lock = new scala.concurrent.Lock
  var queue = new scala.collection.mutable.Queue[String]
  
  def apply(string: String) = {
    lock.acquire
    queue += string
    lock.release
  }

}

case class Call(fct: () => (String, Int), n: Int)

class Caller extends Actor {

  def act() {
    loop {
      react {
        case Call(fun, n) =>
          for (i <- 1 to n-1) fun()
          val (what, result) = fun()
          Main("Caller: " + what + " = " + result)
          sender ! Call(fun, 0)
        case 'stop => exit('normal)
        case unknown => error(unknown.toString)
      }
    }
  }

}

class Scopes(done: OutputChannel[Symbol]) extends Actor {

  var looping = 0

  var localVar1 = 0

  def act() {
    val caller = new Caller
    caller.start
    loop {
      var localVar2 = 0

      def incr1() = {
        localVar1 = localVar1 + 1
        ("localVar1", localVar1)
      }
      def incr2() = {
        localVar2 = localVar2 + 1
        ("localVar2", localVar2)
      }

      if (looping == 0) {
        caller ! Call(incr1, 3)
      }
      caller ! Call(incr2, 3)

      receive {
        case Call(fct, _) => caller ! Call(fct, 3)
        case other => error(other.toString)
      }

      Main("after iteration " + looping + ": ")
      Main("localVar1 = " + localVar1)
      Main("localVar2 = " + localVar2)
      looping = looping + 1

      if (looping > 10) {
        caller ! 'stop
        done ! 'done
        exit('normal)
      }
    }
  }

}

