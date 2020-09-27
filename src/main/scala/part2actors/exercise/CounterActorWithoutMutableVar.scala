package part2actors.exercise

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object CounterActorWithoutMutableVar extends App{

  object CounterActor{

    case object Increment
    case object Decrement
    case object Print
  }

  class CounterActor extends Actor{
    import CounterActor._

    override def receive: Receive = CounterReceive(0)

    def CounterReceive(countValue: Int): Receive = {

      case Increment=>
        println(s"$countValue: incrementing")
      context.become(CounterReceive(countValue + 1))

      case Decrement =>
        println(s"$countValue: decrementing")
        context.become(CounterReceive(countValue -1) )

      case Print => println(s"current value for counter: $countValue")
    }
  }

  val system = ActorSystem("CounterActorDemo")
  val countActor = system.actorOf(Props[CounterActor],"countActor")

  import CounterActor._

  (1 to 5 ).foreach(_ => countActor ! Increment)
  (1 to 3 ).foreach(_ => countActor ! Decrement)
  countActor ! Print
}
