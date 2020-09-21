package part2actors

import akka.actor.{Actor, ActorSystem, Props}

object ActorIntro extends App{

  // part 1 - actor System
    val actorSystem = ActorSystem("firstActorSystem")
    println(actorSystem.name)

// part-2 create actor
// word count actor

  class WordCountActor extends Actor {
    //  internal data
    var totalWords = 0

    //  behavior
    override def receive = {
      case message: String =>
        println(s"[word counter]: I have received $message")
        totalWords += message.split(" ").length
      case msg => println(s"[word counter]: I cant understand ${msg.toString}")
    }

  }
//    part -3 instantiate our actor
    val wordCounter = actorSystem.actorOf(Props[WordCountActor], "wordCounter")

//    part-4 communicate
    wordCounter ! "I am learning akka and its pretty damn cool"


//  lets say you have constructor arguments with the class which are extends Actor

    class Person(name: String) extends Actor{

      override def receive: Receive = {
        case "hi" => println(s"hi, my name is $name")
        case _ =>
      }
    }

  // first way to initiate the actor with this type of class
  val person = actorSystem.actorOf(Props(new Person("sumit")))
  person ! "hi"

  // another way to call an companion object
  object Person {
    def props(name: String) = Props(new Person(name))
  }

  // initiate an actor
  val person2 = actorSystem.actorOf(Person.props("sumit2"))
  person2 ! "hi"

}
