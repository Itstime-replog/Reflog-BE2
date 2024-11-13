FROM openjdk:17-jdk-slim

WORKDIR /app

COPY build/libs/demo-0.0.1-SNAPSHOT.jar /app/application.jar

CMD ["java", "-jar", "/app/application.jar"]