# Code adapted from:
# https://stackoverflow.com/questions/27767264/how-to-dockerize-maven-project-and-how-many-ways-to-accomplish-it

FROM maven:3.8.4-openjdk-17-slim AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package

FROM gcr.io/distroless/java17-debian11
COPY --from=build /home/app/target/kafka-data-provider-0.1.0-SNAPSHOT-jar-with-dependencies.jar /usr/local/lib/kafka-data-provider.jar
ENTRYPOINT ["java", "-jar", "/usr/local/lib/kafka-data-provider.jar"]
