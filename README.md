## About The Project
This is a collection that utilizes the data provider, Kafka producer prototype, and the Kafka consumer prototype found in the subdirectories of this project. To build and run a project individually, see the corresponding subdirectory for further details.
## Getting Started
First acquire this project by cloning the repository. Cloning this repository can be done by downloading [Git](https://git-scm.com/) then executing the command:
```
git clone https://github.com/AutoStreams/streams-prototype-kafka.git
```
The next step is to change the working directory to be the root of the cloned directory, then init and update all submodules of this project recursively. This can be done by executing the commands:

```bash
cd streams-prototype-kafka
git submodule update --init --recursive
```

### Build and run docker-compose 
**Prerequisites**
* Make sure you have downloaded [Docker](https://www.docker.com/) on your system.
* Make sure [Docker Compose](https://docs.docker.com/compose/install/) is installed (Added by default with Docker Desktop for Windows)
* Set the working directory to the root of this collection directory i.e. **`/prototype-kafka/`**

To build the docker images, execute the command:
```bash
docker-compose -f ./docker-compose.yml -f ./broker/docker-compose.yml build
```

To start the built images, execute the command:
```bash
docker-compose -f ./docker-compose.yml -f ./broker/docker-compose.yml up
```
