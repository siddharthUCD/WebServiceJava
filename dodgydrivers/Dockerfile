FROM openjdk:11-jre-slim
COPY target/dodgydrivers-0.0.1-jar-with-dependencies.jar /dodgydrivers-0.0.1.jar
CMD ["java", "-cp", "/dodgydrivers-0.0.1.jar", "service.core.Quoter"]