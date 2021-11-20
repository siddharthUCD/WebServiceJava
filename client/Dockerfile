FROM openjdk:11-jre-slim
COPY target/client-0.0.1-jar-with-dependencies.jar /client-0.0.1.jar
CMD ["java", "-Duser.language=en", "-Duser.Country=IE", "-cp", "/client-0.0.1.jar", "Client"]