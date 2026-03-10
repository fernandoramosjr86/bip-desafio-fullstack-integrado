FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /workspace

COPY pom.xml pom.xml
COPY ejb-module/pom.xml ejb-module/pom.xml
COPY backend-module/pom.xml backend-module/pom.xml
RUN mvn -B -pl backend-module -am dependency:go-offline

COPY ejb-module/src ejb-module/src
COPY backend-module/src backend-module/src
RUN mvn -B -pl backend-module -am package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
RUN apt-get update \
    && apt-get install -y --no-install-recommends curl ca-certificates \
    && rm -rf /var/lib/apt/lists/*
RUN useradd --system --create-home --uid 10001 appuser
COPY --from=build /workspace/backend-module/target/backend-module-0.0.1-SNAPSHOT.jar app.jar
RUN chown appuser:appuser /app/app.jar
USER appuser

EXPOSE 8080
HEALTHCHECK --interval=15s --timeout=5s --start-period=25s --retries=8 \
  CMD curl --fail --silent http://localhost:8080/actuator/health | grep -q '"status":"UP"' || exit 1
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
