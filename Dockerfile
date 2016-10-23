FROM openjdk:8u92-jre-alpine
MAINTAINER Thebora Kompanioni
ADD target/pgmonitor-0.0.1.jar /pgmonitor-0.0.1.jar
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=docker", "/pgmonitor-0.0.1.jar"]
