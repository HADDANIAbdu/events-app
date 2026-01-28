FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copier directement le JAR pré-buildé (nom exact)
COPY target/events-0.0.1-SNAPSHOT.jar events.jar

RUN addgroup -g 1000 appuser && \
    adduser -D -u 1000 -G appuser appuser && \
    chown -R appuser:appuser /app

USER appuser

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "events.jar"]