package part2actors.exercise

import akka.actor.{Actor, ActorSystem, Props}

object CounterActor extends App{

  class CounterActorDemo extends Actor{
    import CounterActorDemo._

    var value = 0
    override def receive: Receive = {

      case Increment(amount) => value = value + amount
      case Decrement(amount) => value = value - amount
      case Print => println(s"current value for counter: $value")
    }
  }

  val system = ActorSystem("CounterActorDemo")
  val countActor = system.actorOf(Props[CounterActorDemo],"countActor")

  object CounterActorDemo{

    case class Increment(amount: Int)
    case class Decrement(amount: Int)
    case object Print
  }

  import CounterActorDemo._
  countActor ! Increment(20)
  countActor ! Decrement(3)
  countActor ! Print

}
