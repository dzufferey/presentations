package sender

import scala.actors.Channel
import scala.actors.OutputChannel
import scala.actors.Actor
import scala.actors.Actor._

object Main extends Channel[Symbol] {

  def main(args: Array[String]): Unit = {
    val evil = new EvilGuy
    val gullible = new Gullible(evil)
    val good = new GoodGuy(this, gullible)
    evil.start
    gullible.start
    good.start
    ?
  }

}

class GoodGuy(done: OutputChannel[Symbol], trusted: Actor) extends Actor {

  var doNotTouch = "forgot to make it private"

  def act() {
    trusted ! 'aMessage
    val reply = ?
    Console.println("secret = " + doNotTouch)
    done ! 'done
  }

}

class Gullible(untrusted: Actor) extends Actor {

  def act() {
    receive[Unit]{ case message: Any =>  untrusted forward message }
  }

}

class EvilGuy extends Actor {

  def act() {
    receive[Unit]{ case doNotCare: Any =>
      sender match {
        case s: GoodGuy =>
          s.doNotTouch = "guess what!"
          s ! 'expectedReply
         case _ =>
          sender ! 'someReply
      }
    }
  }

}
