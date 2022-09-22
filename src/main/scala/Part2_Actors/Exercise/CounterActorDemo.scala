package Part2_Actors.Exercise

import akka.actor.{Actor, ActorSystem, Props}

object CounterActorDemo extends App{

  class Counter extends Actor{

    var count = 0
    override def receive: Receive = {

      case Increment(value) => count = count + value
      case Decrement(value) => count = count - value
      case Print => println(s"final count: $count")
    }
  }

  val system = ActorSystem("CounterActor")
  val counterActor = system.actorOf(Props[Counter], "counterActor")

  case class Increment(value: Int)
  case class Decrement(value: Int)
  case object Print

  counterActor ! Increment(15)
  counterActor ! Decrement(5)
  counterActor ! Print

}
