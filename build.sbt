name := "PersistenActorDemo"

version := "1.0"

scalaVersion := "2.12.1"

val akkaVersion = "2.5.12"

libraryDependencies ++= Seq(
  "com.typesafe.akka"   %% "akka-actor"                 % akkaVersion,
  "com.typesafe.akka"   %% "akka-cluster"               % akkaVersion,
  "com.typesafe.akka"   %% "akka-cluster-sharding"      % akkaVersion,
  "com.typesafe.akka"   %% "akka-persistence"           % akkaVersion,
  "com.typesafe.akka"   %% "akka-persistence-query"     % akkaVersion,
  "com.github.dnvriend" %% "akka-persistence-inmemory"  % "2.5.1.1"     % "test",
  "com.github.dnvriend" %% "akka-persistence-jdbc"      % "3.0.1",
  "com.h2database"      %  "h2"                         % "1.4.196",
  "com.typesafe"        %  "config"                     % "1.3.1"
)