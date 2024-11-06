FROM openjdk:17-jdk-slim
VOLUME /tmp
COPY target/worker-0.0.1-SNAPSHOT.jar worker.jar
ENTRYPOINT ["java","-jar","/worker.jar"]
