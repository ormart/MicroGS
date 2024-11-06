FROM openjdk:17-jdk-slim
VOLUME /tmp
COPY target/api-0.0.1-SNAPSHOT.jar api.jar
ENTRYPOINT ["java","-jar","/api.jar"]
