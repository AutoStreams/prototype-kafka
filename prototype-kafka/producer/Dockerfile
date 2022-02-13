FROM openjdk:17-oracle

COPY pom.xml /usr/local/service/pom.xml
COPY src /usr/local/service/src
COPY mvnw /usr/local/service/mvnw
COPY .mvn /usr/local/service/.mvn

WORKDIR /usr/local/service

RUN ./mvnw package
CMD ["java", "-cp", "target/kafka-prototype-1.0-SNAPSHOT-jar-with-dependencies.jar", "com.klungerbo.Main"]
