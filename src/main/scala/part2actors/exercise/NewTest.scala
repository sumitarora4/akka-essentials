package part2actors.exercise

import java.util.NoSuchElementException

import scala.collection.immutable.Queue
import scala.util.{Failure, Success, Try}

trait Job{
  val customerId: Long
  val uniqueId: Long
  val duration: Int
  def execute():Unit
}

trait JobQueue{
  def pop(): Job
}

trait JobRunner {
  def runner( jobQueue: JobQueue, jobCount: Long): Unit
  val version : String
}



case class JobClass(customerId: Long, uniqueId: Long, duration: Int) extends Job{
  println("inside jobClass")

  override def execute(): Unit = {
    println("job execution start")

    // execution stuff
    println("job execution ends")
  }
}


class JobQueueClass extends JobQueue {
  println("inside JobQueueClass")

  val customerJobQueue: Queue[Job] = Queue[Job]()


  val jbObject1 =  JobClass(10001, 20001, 10)
  val jbObject2 =  JobClass(10002, 20002, 20)
  val jbObject3 =  JobClass(10003, 20003, 30)

  println("jbObject.customerId=="+jbObject1.customerId)

  val enquedCustomerJobQueue = customerJobQueue.enqueue(jbObject1) :+jbObject2 :+jbObject3
  println("customerJobQueue=="+enquedCustomerJobQueue)

  /*print("Queue Elements: ")
  enquedCustomerJobQueue.foreach((element:Job) => println("ele=="+element.customerId+" "))
*/
  override def pop(): Job = {
    // pop stuff
    if (isEmpty) throw new NoSuchElementException("Underflow Exception")
    else {
      val deq = enquedCustomerJobQueue.dequeue
      println("customerJobQueue after pop=="+deq)
      deq._1
    }
  }
  /*  Function to check if queue is empty */
  def isEmpty: Boolean = enquedCustomerJobQueue.isEmpty
}



//case class Report(customerId: Long, uniqueId: Long, duration: Int)

class JobRunnerClass extends JobRunner /*extends JobQueueClass *//*with Job with JobQueue*/ {

  println("inside JobRunnerClass >>>")

  override val version = "536543A4-4077-4672-B501-3520A49549E6"

  override def runner(jobQueue: JobQueue, jobCount: Long): Unit = {

    val job = jobQueue.pop()

    val executionResult = Try( job.execute()) match{
      case Success(value) => jobCount
      case Failure(exception) => println(exception)
    }

    println("execution Result Count=="+executionResult)
  }

}

object NewTest extends App{

  val jobRunnerDemo = new JobRunnerClass
  println(jobRunnerDemo.version)

  val jQObject= new JobQueueClass
  jobRunnerDemo.runner(jQObject, 1)

}

