FROM openjdk:11-jre-slim
COPY target/girlpower-0.0.1-jar-with-dependencies.jar /girlpower-0.0.1.jar
CMD ["java", "-cp", "/girlpower-0.0.1.jar", "service.core.Quoter"]