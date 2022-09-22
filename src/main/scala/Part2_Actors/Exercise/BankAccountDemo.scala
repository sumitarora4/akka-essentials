package Part2_Actors.Exercise

import Part2_Actors.Exercise.BankAccountDemo.Person.LiveTheLife
import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object BankAccountDemo extends App{

  class BankAccount extends Actor{
    import BankAccountActor._

    var balance = 0
    override def receive: Receive = {
      case Deposit(amount) =>
        if(amount < 0 ){
          sender() ! Failure("Negative Amount Not allowed ")
        }
        else{
          balance += amount
          sender() ! Success("Deposit Successful")
        }
      case Withdraw(amount) =>
        if (amount < balance) {
          balance -= amount
          sender() ! Success("WithDraw Successful")
        }
        else {
          sender() ! Failure("Insufficient Balance")
        }
      case Statement => sender() ! s"balance: $balance"
     }
  }

  object BankAccountActor{
    case class Withdraw(amount: Int)
    case class Deposit(amount: Int)
    case object Statement

    case class Success(message: String)
    case class Failure(message: String)
  }

  val system = ActorSystem("BankAccountSystem")
  val bankAccountActor = system.actorOf(Props[BankAccount], "bankAccountActor")


  // Doing Transaction between Person and BankAccount
  object Person {
    case class LiveTheLife(account: ActorRef)
  }

  class Person extends Actor{

    import BankAccountActor._
    import Person._
    override def receive: Receive = {

      case LiveTheLife(account) => {
        account ! Deposit(-100)
        account ! Withdraw(500)
        account ! Withdraw(10)
        account ! Statement
      }
      case message => println(s"message:${message.toString}")
    }
  }

  val personActor = system.actorOf(Props[Person], "pesonActor")
  personActor ! LiveTheLife(bankAccountActor)

}
