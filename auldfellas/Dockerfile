FROM openjdk:11-jre-slim
COPY target/auldfellas-0.0.1-jar-with-dependencies.jar /auldfellas-0.0.1.jar
CMD ["java", "-cp", "/auldfellas-0.0.1.jar", "service.core.Quoter"]