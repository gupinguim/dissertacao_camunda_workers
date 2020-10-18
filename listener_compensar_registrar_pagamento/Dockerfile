#
# Build stage
#
FROM maven:3.6.0-jdk-11-slim AS build
ENV JAVA_HOME C:\Program Files\Java\jdk1.8.0_181
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package


FROM openjdk:8
MAINTAINER  Augusto Correia <augusto.a.correia@gmail.com>
ENV JAVA_HOME C:\Program Files\Java\jdk1.8.0_181
COPY --from=build /home/app/target/camunda-listener-compensar-registrar-pagamento-0.0.1-SNAPSHOT.jar /usr/local/lib/camunda-listener-compensar-registrar-pagamento.jar
ENTRYPOINT ["java","-jar","/usr/local/lib/camunda-listener-compensar-registrar-pagamento.jar"]

