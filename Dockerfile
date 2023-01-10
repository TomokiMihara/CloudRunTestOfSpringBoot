FROM maven:3.8.3-openjdk-17 AS builder
WORKDIR /tmp

COPY ./src ./src
COPY ./pom.xml .
RUN mvn package

FROM openjdk:17-alpine AS run
COPY --from=builder /tmp/target/app.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]