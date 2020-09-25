package part2actors

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
//import akka.actor.Actor.Receive

object ActorCapabilities extends App {

  class SimpleActor extends Actor {

    def receive: Receive = {
      case "Hi" => sender() ! "Hello there" // reply to a message
      case message: String => println(s"[$self]: I have recieved: $message")
      case number: Int => println(s"[Simple Actor]: I have recieved: $number")
      case SpecailMessage(content) => println(s"[Simple Actor]: I have recieved: $content")
      case SendMessagetoYourself(content) => self ! content
      case SayHiTo(ref) => ref ! "Hi"
      case WirelessPhoneMesage(content, ref) => ref forward(content + " sumit")
    }

  }
  val system = ActorSystem("actorCapabilitiesDemo")
  val simpleActor = system.actorOf(Props[SimpleActor], "simpleActor")
  simpleActor ! "Hi!"
  simpleActor ! 31

  case class SpecailMessage(content: String)
  simpleActor ! SpecailMessage("speical case class message")

  // 1- message can be of any type
  // message must be immutable
  // message must be serializable
  // in practice use case class and case objects

  // 2- actor have information about their context and about themselves

  case class SendMessagetoYourself(content: String)
  simpleActor ! SendMessagetoYourself(" message sendign to yourself")

  // 3- actor can reply to message

  val alice: ActorRef = system.actorOf(Props[SimpleActor], "alice")
  val bob: ActorRef = system.actorOf(Props[SimpleActor],"bob")

  case class SayHiTo(ref: ActorRef)
  alice ! SayHiTo(bob)

  // 4- dead letters
  alice ! "Hi"

  //5- forward message

  case class WirelessPhoneMesage(content: String, ref: ActorRef)
  alice ! WirelessPhoneMesage("Hi" , bob)



}

