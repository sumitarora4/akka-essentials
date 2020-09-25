package part2actors

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import part2actors.ChangingActorBehavior.Mom.MomStart

object ChangingActorBehavior extends App{

  object FussyKid{
    case object KidAccept
    case object KidReject
    val HAPPY = "Happy"
    val SAD = "Sad"
  }
  class FussyKid extends Actor{
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

  }

  object Mom{
    case class MomStart(ActRef: ActorRef)
    case class Food(food: String)
    case class Ask(message: String)
    val VEGETABLES = "vegges"
    val CHOCOLATE = "chocolate"

  }
  class Mom extends Actor{
    import FussyKid._
    import Mom._

    override def receive: Receive = {
      case MomStart(kidRef) =>
        kidRef ! Food("vegges")
        kidRef ! Ask("do you wan to play?")
      case KidAccept => println("he is happy")
      case KidReject => println("he is sad but atleast he is healthy")
    }
  }

  val system = ActorSystem("ChangingActorBehaivor")

  val fussyKidActor = system.actorOf(Props[FussyKid])
  val momActor = system.actorOf(Props[Mom])

  momActor ! MomStart(fussyKidActor)

}
