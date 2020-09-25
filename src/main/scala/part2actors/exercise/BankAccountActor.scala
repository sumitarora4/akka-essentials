package part2actors.exercise

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object BankAccountActor extends App{

  class BankAccountDemo extends Actor{

    var balance = 0

    import BankAccountDemo._
    override def receive: Receive = {
      case WithDraw(amount) =>
        if(amount < 0)
          sender() ! TransactionFailure("invalid withdraw amount")
        else if(amount > balance)
          sender() ! TransactionFailure("insufficient fund")
        else
        balance -= amount
        sender() ! TransactionSuccess(s"Transaction WithDrew Success: $balance")

      case Deposit(amount) =>
        if(amount < 0)
          sender() ! TransactionFailure("invalid deposit amount")
        else
        balance += amount
        sender() ! TransactionSuccess(s"Transaction Deposit Success: $balance")
      case Statement => println(s"[BankAccountDemo]: final balance is $balance")
    }
  }

  object BankAccountDemo {
    case class WithDraw(amount: Int)
    case class Deposit(amount: Int)
    case object Statement

    case class TransactionSuccess(message: String)
    case class TransactionFailure(reason: String)
  }

  val system = ActorSystem("BankAccountDemo")
  val bankAccountActor = system.actorOf(Props[BankAccountDemo], "backAccountActor")


  class Person extends  Actor {
    import Person._
    import BankAccountDemo._

    override def receive: Receive = {
      case LiveTheLife(bankAccountActor) =>
        bankAccountActor ! WithDraw(90000)
        bankAccountActor ! Deposit(10000)
        bankAccountActor ! WithDraw(500)
        bankAccountActor ! Statement
      case message: String => println(message)
    }
  }

  object Person{
    case class LiveTheLife(account: ActorRef)
  }

  val personActor = system.actorOf(Props[Person], "personActor")

  import Person._
  personActor ! LiveTheLife(bankAccountActor)
  /*
  import BankAccountDemo._
  bankAccountActor ! WithDraw(32)
  bankAccountActor ! Deposit(50)
  bankAccountActor ! Statement
*/
}
