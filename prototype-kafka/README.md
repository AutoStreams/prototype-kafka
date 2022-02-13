[![Contributors][contributors-shield]][contributors-url]

# Kafka Prototype

## Getting Started

### Option 1: Build and run with Maven

#### Prerequisites
* Download the latest version of Maven.
* Download a Java JDK of version 17
* Set the working directory to the root of the producer project i.e. **`streams-prototypes/prototype-kafka/producer/`**


To build the project with its dependencies to a single jar file, execute the command:
```
mvn package
```

To run the application, execute the command:
```
java -cp target/kafka-prototype-1.0-SNAPSHOT-jar-with-dependencies.jar com.klungerbo.Main
```

### Option 2: Build and run with Docker
* Make sure you have downloaded Docker on your system.
* Set the working directory to the root of the producer project i.e. **`streams-prototypes/prototype-kafka/producer/`**

To build the docker image, execute the command:
```
docker build -t kafka-prototype-producer
```

To execute the built image, execute the command:
```
docker run -it kafka-prototype-producer
```


[contributors-shield]: https://img.shields.io/github/contributors/klungerbo/streams-prototypes
[contributors-url]: https://github.com/Klungerbo/streams-prototypes/graphs/contributors