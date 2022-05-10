## About The Project
This is a Kafka producer prototype that uses a Netty server to receive messages from Netty clients. The received messages are delegated to a Kafka broker. 
## Getting Started
First acquire this project by cloning the repository. Cloning this repository can be done by downloading [Git](https://git-scm.com/) then executing the command:
```
git clone https://github.com/AutoStreams/prototype-kafka.git
```
### Option 1: Build and run with Maven
**Prerequisites**
* Download the latest version of [Maven](https://maven.apache.org/).
* Download a Java JDK of version 17
* Set the working directory to the root of this Kafka prototype producer project i.e. **`prototype-kafka/producer/`**
To build the project with its dependencies to a single jar file, execute the command:
```
mvn package
```
To run the application, execute the command:
```
java -jar kafka-producer-0.1.0-jar-with-dependencies.jar 
```
### Option 2: Build and run with Docker
**Prerequisites**
* Make sure you have downloaded [Docker](https://www.docker.com/) on your system.
* Set the working directory to the root of this Kafka prototype producer project i.e. **`prototype-kafka/producer/`**

To build the docker image, execute the command:
```
docker build -t kafka-prototype-producer .
```

To execute the built image, execute the command:
```
docker run -it kafka-prototype-producer
```
