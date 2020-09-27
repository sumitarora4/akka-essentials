package part2actors

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import part2actors.ChangingActorBehavior.Mom.MomStart

object ChangingActorBehavior extends App{
/*
  object FussyKid{
    case object KidAccept
    case object KidReject
    val HAPPY = "Happy"
    val SAD = "Sad"
  }*/


  /*class FussyKid extends Actor{
    import FussyKid._
    import Mom._
    var state = HAPPY

    override def receive: Receive = {

      case Food(VEGETABLES) => state = SAD
      case Food(CHOCOLATE) => state = HAPPY
      case Ask(_) =>
        if (state == HAPPY) sender() ! KidAccept
        else sender() ! KidReject
    }

  }*/

  // for removing var variable state as this should be less mutable
  // best approach is change the state of an actor with context.become method

  object StatelessFussyKid{
    case object KidAccept
    case object KidReject
  }

  class StatelessFussyKid extends  Actor {

    import StatelessFussyKid._
    import Mom._
    override def receive: Receive = happyReceive

    def happyReceive : Receive = {

      case Food(VEGETABLES) => context.become(sadReceive, true)  // change the state of actor state
      case Food(CHOCOLATE) =>
      case Ask(_) => sender() ! KidAccept

    }

    def sadReceive : Receive = {

      case Food(VEGETABLES) => context.become(sadReceive, true)
      case Food(CHOCOLATE) => context.unbecome()
      case Ask(_) => sender() ! KidReject
    }
  }

  object Mom{
    case class MomStart(ActRef: ActorRef)
    case class Food(food: String)
    case class Ask(message: String)
    val VEGETABLES = "vegges"
    val CHOCOLATE = "chocolate"

  }
  class Mom extends Actor{
    import StatelessFussyKid._
    import Mom._

    override def receive: Receive = {
      case MomStart(kidRef) =>
        kidRef ! Food("vegges")
        kidRef ! Food("vegges")
//        kidRef ! Food("chocolate")
//        kidRef ! Food("chocolate")

        kidRef ! Ask("do you wan to play?")
      case KidAccept => println("he is happy")
      case KidReject => println("he is sad but at least he is healthy")
    }
  }

  val system = ActorSystem("ChangingActorBehaivor")

//  val fussyKidActor = system.actorOf(Props[FussyKid])
  val momActor = system.actorOf(Props[Mom])
  val statelessFussyKidActor =system.actorOf(Props[StatelessFussyKid])


//  momActor ! MomStart(fussyKidActor)
    momActor ! MomStart(statelessFussyKidActor)

  /* mom receives MomStart
  * kid receives Food(vegges)  -> kid will change the handler to sad handler
  * kid receives Ask(do you wan to play) -> kid replies with sadReceives handler
  * mom receives KidReject
  *
  * */



}
