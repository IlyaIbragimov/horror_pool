FROM eclipse-temurin:21-jre
LABEL authors="ilyam"
WORKDIR /app
COPY target/horror_pool-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]