package com.mattv.sharding

import akka.actor.{Props, ReceiveTimeout}
import akka.cluster.sharding.ShardRegion
import akka.persistence.PersistentActor
import scala.concurrent.duration._
import ShardRegion.Passivate

case object Increment
case object Stop
final case class Message(id: String, cmd: Any)
final case class CounterChanged(delta: Int)

object PersistentClusteredActor {
  def props = Props(new PersistentClusteredActor)

  val shardName: String = "PersistentClusteredActor"

  val numberOfShards = 100

  val idExtractor: ShardRegion.ExtractEntityId = {
    case message: Message => (message.id, message.cmd)
  }

  val shardResolver: ShardRegion.ExtractShardId = {
    case msg: Message => (math.abs(msg.id.hashCode) % numberOfShards).toString
  }
}

class PersistentClusteredActor extends PersistentActor {
  // Set a timeout for passivization
  context.setReceiveTimeout(30.seconds)

  var count = 0

  def updateState(event: CounterChanged): Unit =
    count += event.delta

  override def receiveRecover: Receive = {
    case evt: CounterChanged =>
      updateState(evt)
      println("Received recover event for " + self.path.name + ". Count is now " + count)
  }

  override def receiveCommand: Receive = {
    case Increment => persist(CounterChanged(+1)){ evt => {
      updateState(evt)
      println("Increment " + self.path.name + ". Count is now " + count)
    }}
    case ReceiveTimeout => context.parent ! Passivate(stopMessage = Stop)
    case Stop => context.stop(self)
  }

  override def preStart(): Unit = {
    println("ClusteredActor prestart " + self.path.name)
  }

  override def postStop(): Unit = {
    println("stopping " + self.path.name + ". Final count " + count)
    super.postStop()
  }


  override def persistenceId: String = "PersistentClusteredActor-" + self.path.name
}
