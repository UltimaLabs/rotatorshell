# RotatorShell, a shell frontend for rotctld

RotatorShell lets you manually adjust antenna rotator position through `rotctld` backend.

## Built With

* [Spring Boot 2](https://spring.io/projects/spring-boot/)
* [Spring Shell](https://projects.spring.io/spring-shell/)
* [Project Lombok](https://projectlombok.org/)
* [Gradle Build Tool](https://gradle.org/)

## Getting Started

### Prerequisites

* [JDK 1.8 or higher](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)

### Installing

Checkout the code.

```
$ git clone https://github.com/UltimaLabs/rotatorshell.git
```

### Building and running the Spring Boot application

To run the application, execute the following Gradle command:
```
./gradlew bootRun
```

To build the executable jar, run the following command::
```
./gradlew bootJar
```
Gradle uses `jgitver`, a plugin which provides a standardized way, via a library, to calculate a project [semver](http://semver.org) compatible version from a git repository and its content.

The executable jar is located in the `build/libs` directory. You can run it by executing the following command (adjust version as needed):
```
java -jar build/libs/rotatorshell-0.0.0-0.jar
```

## Configuration

Local development application is configured by editing the `src/main/resources/application.yml` file.

## Help

Type `help` after starting the RotatorShell application to get the list of available commands. Enter `help <command>` to display help about individual command.
 