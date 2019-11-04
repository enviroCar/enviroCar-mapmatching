# build stage
FROM maven:3-jdk-8-alpine as BUILD
COPY ./ /usr/src/app/

RUN mvn -f /usr/src/app/pom.xml --batch-mode --errors --fail-fast \
    --define maven.javadoc.skip=true \
    --define skipTests=true \
    clean package

# app stage
FROM openjdk:13-jdk-alpine
LABEL maintainer="52North"

ARG SERVER_VERSION=0.0.1-SNAPSHOT

COPY --from=BUILD /usr/src/app/target/mapmatching-${SERVER_VERSION}.jar /usr/src/app/app.jar
HEALTHCHECK --interval=30s --timeout=30s --start-period=5s --retries=3 \
    CMD wget http://localhost:8080/actuator/health -q -O - > /dev/null 2>&1

EXPOSE 8080

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/usr/src/app/app.jar"]
