# QuantumEvolutionGameServer
A game server written in Java with Guice, Grpc maven project which contains individual socket management, stream processing and mvc model, also with a router inside of this framework.

It's easy to use.

## Start Server
Start server in `src/main/java/evolution/server/GameServer.java`

## Some Hints
* Route path config are added in file `src/main/java/conf/evolution.routes`
* At present, all route method are handled by method in `src/main/java/evolution/server/handler/RequestHandler.java`
* Server logic are all settled in `src/main/java/evolution/server`
* All common components in `src/main/java/evolution/components`
* Your game logic may include two part, **Game Service Logic** and **Game Logic**
    * Game service management logic in `src/main/java/evolution/service`
    * All game logic in `src/main/java/evolution/game`

## Grpc Part
* Protobuf files in `src/main/proto`

## Whats' more

`Unit Test` and `Integration Test` is good a practice, let's enjoy it!
