# Bobby

## Requeriments

- Java 1.7.x
- Gradle 1.6

## Test

`$ gradle test`

## Run

`$ gradle jettyRun`

## Configurations

The only configuration for the application is the Neo4J database path. 

It's located at:

- src/main/resources/context.xml (Production)
- src/test/resources/bobby-servlet-test.xml (Tests)

You must create the path before run the tests or the application.

## Log

The log library is LogBack and the configurations is at:

- src/main/resources/logback.xml

