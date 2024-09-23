# Rock Paper Scissors

## Requirements
Background

You are a senior developer at ACME Games an online casual gaming company. The company has more than 1 million daily active users playing games online. The site however does not have a game of rock, paper, scissors. This is now the most requested game by the users of the site. The CTO of the company wants you to develop the game and deploy it to production.

Task

Write a program that plays Rock, Paper, Scissors against a human. Try to exploit that humans are very bad at generating random numbers. You are only required to code the server-side components.

Deliverable

The assignment should be delivered as a web application that allows the user to start the game, make moves, terminate the game and observe the statistics. A user interface is not expected.

This is an open assignment in terms of how you structure the solution. You will be judged on the overall quality of the code (simplicity, presentation, performance).

## Assumptions

- I was initially going with websockets, but given the nature of the game, a bit of latency between moves is probably not going to be noticed, so I chose http for simplicity and time constraints
  - Given I chose http, I introduced Redis to store intermediate results in memory and not go to disk every time
  - We can safely scale out the application that we will be able to handle concurrent users with this approach
- The requirements are not clear regarding if users are guests or are authenticated, so I created two endpoints to create and authenticate users with some simple authentication logic
  - I left out other CRUD operations given it is not the focus of the assignment
- Given there's no much time left, I created integration tests for both domains, but only created unit tests for the game service layer, leaving out other domains and also controllers unit tests
- The move prediction logic can be improved by far if some algorithm or IA is added to identify patterns given historical moves, but given my limitations on this area, I went with a very simple approach of using the last two rounds to determine a possible counter play

## Technologies
- Java 21
- Spring Boot 3.3
- Postgres
- Redis
- Gradle
- Docker

## Running

### Requirements
- JDK 21
- Docker environment

### Steps to run
- From project root run `./gradlew clean build`
- Then `docker-compose up -d`
- Finally `./gradlew bootRun --args='--spring.profiles.active=local'`
- Endpoint reference can then be found at http://localhost:8080/swagger-ui/index.html