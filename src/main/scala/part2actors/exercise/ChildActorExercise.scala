package part2actors.exercise

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object ChildActorExercise extends App{


  object WordCounterMaster{
    case class Initialize(nChildern: Int)
    case class WordCountTask(id: Int, text: String)
    case class WordCountReply(id: Int, count: Int)
  }
  class WordCounterMaster extends Actor{

    import WordCounterMaster._
    override def receive: Receive = {

      case Initialize(n) =>

        println("[masters]Initializing Masters...")
        val childrenRefs =  for(i <- 1 to n) yield context.actorOf(Props[WordCounterWorker],s"workerActor_$i")
        context.become(withChildren(childrenRefs, 0, 0, Map()))
    }

    def withChildren(childrenRefs: Seq[ActorRef], currentChildIndex: Int, currentTaskId: Int, requestMap:Map[Int, ActorRef]): Receive ={
      case text: String =>

        println(s"[masters]I have recieved Text: $text - I will send it to child $currentChildIndex")
        val originalSender = sender()
        val task = WordCountTask(currentTaskId, text)
        val childRef = childrenRefs(currentChildIndex)

        childRef ! task

        val nextChildIndex = (currentChildIndex + 1) % childrenRefs.length
        val nextTaskId = currentTaskId + 1
        val newRequestMap = requestMap + (currentTaskId -> originalSender)

        context.become(withChildren(childrenRefs, nextChildIndex, nextTaskId,newRequestMap ))



      case WordCountReply(id, count) =>
        println(s"[masters] I have received a reply for task $id with count $count")
        val secondOriginalSender= requestMap(id)
        secondOriginalSender ! count
        context.become(withChildren(childrenRefs, currentChildIndex, currentTaskId, requestMap - id))
    }
  }

  class WordCounterWorker extends Actor{
    import WordCounterMaster._
    override def receive: Receive = {

      case WordCountTask(id, text) =>
        println(s"${self.path} I have received task $id with $text")
        sender ! WordCountReply(id, text.split(" ").length)
    }
  }



  class TestACtor extends Actor{

    import WordCounterMaster._
    override def receive: Receive = {

      case "go" =>
        val master = context.actorOf(Props[WordCounterMaster], "master")
        master ! Initialize(3)

        val texts = List("I love akka", "yes", "for sure", "Scala is super dope")
        texts.foreach( text => master ! text)
      case count: Int =>
        println(s" I have received a reply: $count")
    }
  }

  val system = ActorSystem("WordCounterDemo")

  val testActor = system.actorOf(Props[TestACtor], "testActor")
  testActor ! "go"



}
