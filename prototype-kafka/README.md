## About The Project
This is a collection that utilizes the data producer, Kafka producer prototype, and the Kafka consumer prototype found in the subdirectories of this directory. To build and run a project individually, see the corresponding subdirectory for further details.
## Getting Started
First acquire this project by cloning the repository. Cloning this repository can be done by downloading [Git](https://git-scm.com/) then executing the command:
```
git clone https://github.com/Klungerbo/streams-prototypes.git
```
### Build and run docker-compose 
**Prerequisites**
* Make sure you have downloaded [Docker](https://www.docker.com/) on your system.
* Download a Java JDK of version 17
* Set the working directory to the root of this Kafka prototype producer project i.e. **`streams-prototypes/prototype-kafka/kafka-prototype-producer/`**
  To build the project with its dependencies to a single jar file, execute the command:
```
mvn package
```
To run the application, execute the command:
```
java -jar kafka-prototype-producer-1.0-SNAPSHOT-jar-with-dependencies.jar 
```
### Option 2: Build and run with Docker
**Prerequisites**
* Make sure you have downloaded [Docker](https://www.docker.com/) on your system.
* Make sure [Docker Compose](https://docs.docker.com/compose/install/) is installed (Added by default with Docker Desktop for Windows)
* Set the working directory to the root of this collection directory i.e. **`streams-prototypes/prototype-kafka/`**

To build the docker images, execute the command:
```
docker-compose build
```

To execute the built images, execute the command:
```
docker-compose up
```
