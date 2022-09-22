package Part2_Actors

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object ActorCapabilities extends App{

  class SimpleActor extends Actor {
    override def receive: Receive = {
      case "Hi!" => sender() ! "Hello There" // replying to the message
//      case "Holas" => sender() ! "Not Holas, its Hola"
      case message: String => println(s"${context.self}: $message")
      case number: Int => println(s"[SimpleActor]: I have a number: $number")
      case SpecialMessage(content) => println(s"[SimpleActor]: I have a speical Message: $content")
      case SendMessageToYourself(content) => self ! content

      case SayHiTo(ref) => ref ! "Hi!"
      case WirelessPhone(content, ref) => ref forward content + "s"

    }
  }

  val system = ActorSystem("actorCapabilities")
  val simpleActor = system.actorOf(Props[SimpleActor], "SimpleActor")

  // 1) messages can be of any type
  // a) messages must be IMMUTABLE
  // b) messages must be SERIALIZABLE
  // C) in practice use case classes and case objects

  simpleActor ! "Hello world"
  simpleActor ! 42

  case class SpecialMessage(content: String)
  simpleActor ! SpecialMessage("you are special")

  // 2) actors have the information about their context and themselves
  // context.self == this in OOP

  case class SendMessageToYourself(content: String)
  simpleActor ! SendMessageToYourself("I have a proud to be an actor")

  // 3) actors can reply to messages
  val alice = system.actorOf(Props[SimpleActor], "alice")
  val bob = system.actorOf(Props[SimpleActor], "bob")
  val sumit = system.actorOf(Props[SimpleActor], "sumit")

  case class SayHiTo(ref: ActorRef)
  alice ! SayHiTo(bob)

  // 4) dead letters
  alice ! "Hi!" // this will go to case "Hi!" in which sender is null so alice actor will generate dead letters
//  means no sender available
  sumit ! "this is sumit" // this will go to case message: String in which implicit sender is available


  // 5) forwarding message
  // forwarding = sending a message with original sender
  case class WirelessPhone(content: String, ref: ActorRef)
  alice ! WirelessPhone("Hola", bob)


}
