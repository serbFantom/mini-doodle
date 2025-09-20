FROM openjdk:24-jdk-slim

WORKDIR /app

COPY target/miniDoodle-0.0.1-SNAPSHOT.jar /app/miniDoodle.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/miniDoodle.jar"]
