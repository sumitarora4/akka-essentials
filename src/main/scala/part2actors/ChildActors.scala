package part2actors

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import part2actors.ChildActors.Parent.{CreateChild, TellChild}

object ChildActors extends App {

  object Parent{
    case class CreateChild(name: String)
    case class TellChild(message: String)
  }

  class Parent extends Actor{

    import Parent._
    override def receive: Receive = {

      case CreateChild(name) => println(s"${self.path} creating child: $name")
        val childActorRef = context.actorOf(Props[Child], name)
        context.become(withChild(childActorRef))
    }

    def withChild(childActorRef: ActorRef): Receive = {
      case TellChild(message) => childActorRef forward(message)
    }

  }

  class Child extends Actor {

    override def receive: Receive = {
      case message => println(s"${self.path} I got:$message")
    }
  }

  val system = ActorSystem("ParentChildDemo")
  val parent = system.actorOf(Props[Parent],"parent")

  parent ! CreateChild("child")
  parent ! TellChild("Hey Child, How are you?")


  /**
    * Actor Selection
    */

  val childSelection = system.actorSelection("/user/parent/child2")
    childSelection ! "I found you"
}

