FROM gradle:7.5-jdk11-alpine AS gradle-build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon

FROM openjdk:11-jre-slim
EXPOSE 8080
RUN mkdir /repository-api
COPY --from=gradle-build /home/gradle/src/build/libs/*.jar /repository-api/repository-api.jar
ENTRYPOINT ["java", "-jar", "/repository-api/repository-api.jar"]