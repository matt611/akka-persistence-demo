package com.mattv.sharding

import akka.actor.{ActorSystem, Props}
import akka.cluster.sharding.{ClusterSharding, ClusterShardingSettings, ShardRegion}
import com.typesafe.config.ConfigFactory

case class MessageWrapper(id: Int, message: Message)

final case class Get(counterId: Long)
final case class EntityEnvelope(id: Long, payload: Any)

object Application {
  def main(args: Array[String]): Unit = {
    val port: String = args(0)
    println("Starting cluster listening on port " + port)

    // set the ports in the config
    val config = ConfigFactory.parseString(
      s"""
        akka.remote.netty.tcp.port=$port
        akka.remote.artery.canonical.port=$port
        """).withFallback(ConfigFactory.load())

    // all actor systems who wish to join this cluster must have the same name
    val system = ActorSystem("PersistentActorDemo", config)

    ClusterSharding(system).start(
      typeName = PersistentClusteredActor.shardName,
      entityProps = Props[PersistentClusteredActor],
      settings = ClusterShardingSettings(system),
      extractEntityId = PersistentClusteredActor.idExtractor,
      extractShardId = PersistentClusteredActor.shardResolver
    )

    if (port != "2551" && port != "2552") {
      println("start the bot")
      system.actorOf(Props[Bot], "bot")
    }
  }
}
