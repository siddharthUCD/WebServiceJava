FROM openjdk:11-jre-slim
COPY target/broker-0.0.1-jar-with-dependencies.jar /broker-0.0.1.jar
CMD ["java", "-cp", "/broker-0.0.1.jar", "service.core.Broker"]