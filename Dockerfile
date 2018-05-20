FROM openjdk:8-jdk-alpine
COPY build/libs/kwetter-0.0.1-SNAPSHOT.jar app.jar
ENV JAVA_OPTS=""
ENTRYPOINT exec java -jar /app.jar --debug