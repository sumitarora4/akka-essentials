package part2actors

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.event.Logging

object ActorLoggingDemo extends App {

  class SimpleActorWithExplicitLogger extends  Actor{

    //1- Explicit Logger
    val logger = Logging(context.system,this)
    override def receive: Receive = {
      case message => logger.info(message.toString)
    }
  }

  val system = ActorSystem("LoggingDemo")
  val actor = system.actorOf(Props[SimpleActorWithExplicitLogger],"SimpleActorExplicitLogger")

  actor ! "My Logger message"


  //2- ActorLogging

  class SimpleActorWithActorLogging extends Actor with ActorLogging{

    override def receive: Receive = {

      case (x, y) => log.info("hey name is {} and my birthdate {} ", y, x)
      case message => log.info(message.toString)
    }
  }

  val simpleActorLogging = system.actorOf(Props[SimpleActorWithActorLogging],"SimpleActorLogging")
  simpleActorLogging ! "My another Logger message"
  simpleActorLogging ! (31, "Sumit")


}
