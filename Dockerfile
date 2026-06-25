FROM eclipse-temurin:21-jre
LABEL authors="ilyam"
WORKDIR /app

RUN groupadd --system app \
    && useradd --system --gid app --home-dir /app --shell /usr/sbin/nologin app

COPY --chown=app:app target/horror_pool-0.0.1-SNAPSHOT.jar app.jar

USER app
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]