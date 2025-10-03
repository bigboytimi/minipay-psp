FROM maven:3.9.6-eclipse-temurin-17-alpine AS build
LABEL maintainer="Timilehin Olowookere"
RUN mkdir -p /minipay
WORKDIR /minipay
COPY pom.xml /minipay
COPY src /minipay/src
RUN mvn -B clean package -Dmaven.test.skip=true --file pom.xml

FROM openjdk:17-jdk-slim

RUN mkdir -p /apps
RUN mkdir -p /apps/config

WORKDIR /apps


COPY --from=build /minipay/target/*.jar /apps/minipay.jar
COPY --from=build /minipay/src/main/resources/application.properties /apps/config/application.properties

RUN pwd

RUN ls -ahl && ls -ahl /apps/config
VOLUME /apps/config

RUN apt-get update && apt-get install -y tzdata


EXPOSE 8082
ENTRYPOINT ["java","-jar","/apps/minipay.jar"]