package Part2_Actors

import akka.actor.{Actor, ActorSystem, Props}

object ActorsIntro extends App{

//  part -1 : define an actor system
  val actorSystem = ActorSystem("FirstActorSystem")
  println(actorSystem.name)

//  part2 - create an actor which has recieve method
  class WordCounterActor extends Actor {
//  internal data
    var totalWords = 0

//  behavior
    def receive: PartialFunction[Any, Unit] = {
      case message: String => println(s"[word counter]: I have recieved $message")
        totalWords += message.split(" ").length
      case msg => println(s"[word counter] I cannot understand ${msg.toString}")
    }
  }

//  part3 - instantiate an actor
  val wordCounter = actorSystem.actorOf(Props[WordCounterActor], "wordCounter")

//  part4 - communicate
  wordCounter ! "I'm learning Akka and it's interesting"
}
