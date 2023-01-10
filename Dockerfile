FROM maven:3.8.3-openjdk-17
WORKDIR /tmp

COPY ./src ./src
COPY ./pom.xml .
RUN mvn package

FROM khipu/openjdk17-alpine:debianslim-jre
COPY --from=builder /tmp/target/app.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]