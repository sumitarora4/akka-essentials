package part4faultTolerance

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, PoisonPill, Props, Terminated}

object StartingStoppingActors extends App{

  val system = ActorSystem("stoppingActorDemo")

  object Parent{

    case class StartChild(name: String)
    case class StopChild(name: String)
    case object Stop
  }

  class Parent extends Actor with ActorLogging{

    import Parent._

    override def receive: Receive = withChildren(Map())

    def withChildren(children: Map[String, ActorRef]): Receive= {

      case StartChild(name) => log.info(s"Starting child $name")
        context.become(withChildren(children + (name -> context.actorOf(Props[Child], name)) ))

      case StopChild(name) => log.info(s"stoping child wiht the name $name")
        val optionChildRef = children.get(name)
        optionChildRef.foreach(childRef => context.stop(childRef))

      case Stop => log.info(s"Stopping parent and child")
        context.stop(self)

      case message => log.info(message.toString)

    }
  }

  class Child extends Actor with ActorLogging {

    override def receive: Receive = {
      case message => log.info(message.toString)
    }
  }

  import Parent._
  /*val parent = system.actorOf(Props[Parent],"parent")
  parent ! StartChild("child1")

  val child = system.actorSelection("/user/parent/child1")
  child ! "hey Kid!!!"

  parent ! StopChild("child1")
//  for ( _ <-1 to 20) child ! "r u still there ?"


  parent ! StartChild("child2")

  val child2 = system.actorSelection("/user/parent/child2")
  child2 ! "hey second kid"

  parent ! Stop
  for ( _ <- 1 to 20) parent ! "parent r u there ?"
  for ( i <- 1 to 100) child2 ! s"[$i]second kid u there ?"*/


  /**
    * Death Watcher
    */

  class Watcher extends  Actor with ActorLogging{

    override def receive: Receive = {

      case StartChild(name) => val child = system.actorOf(Props[Child], name)
        log.info(s"started and watching the child $name")
        context.watch(child)
      case Terminated(ref) => log.info(s"the reference that I watching $ref has been stopped. ")
    }
  }

  val watchedActor = system.actorOf(Props[Watcher], "watcher")

  watchedActor ! StartChild("watchedChild")

  val watchedChildActor = system.actorSelection("/user/watcher/watchedChild")

  Thread.sleep(500)
  watchedChildActor ! PoisonPill


}
