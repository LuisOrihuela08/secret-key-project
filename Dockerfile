FROM amazoncorretto:17-alpine-jdk
WORKDIR /app
EXPOSE 8080
COPY ./target/secret-key-project-0.0.1-SNAPSHOT.jar secret-key-project.jar

ENTRYPOINT [ "java", "-jar", "secret-key-project.jar"]

#FROM maven:3.9-eclipse-temurin-17-alpine AS build
#WORKDIR /app
#COPY pom.xml .
#COPY src ./src
#RUN mvn clean package -DskipTests

#FROM amazoncorretto:17-alpine-jdk
#WORKDIR /app
#EXPOSE 8080
#COPY --from=build /app/target/secret-key-project-0.0.1-SNAPSHOT.jar secret-key-project.jar
#ENTRYPOINT ["java", "-jar", "secret-key-project.jar"]