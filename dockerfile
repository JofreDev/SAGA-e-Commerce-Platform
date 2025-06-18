FROM gradle:8.5-jdk21-alpine AS builder

WORKDIR /app

# Copiamos lo esencial
COPY settings.gradle .
COPY build.gradle .
COPY gradle gradle

# Copiamos el código fuente completo
COPY . .

# Construimos el JAR del subproyecto app-service (desde el contexto raíz)
RUN gradle :app-service:build --no-daemon

# Imagen final
FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

# Copiar el JAR desde el build
COPY --from=builder /app/ms_product-management_service/applications/app-service/build/libs/*.jar app.jar
COPY deployment/opentelemetry-javaagent.jar /app/opentelemetry-javaagent.jar

# Definir entrada con el agente
ENTRYPOINT ["java", 
  "-javaagent:/app/opentelemetry-javaagent.jar",
  "-Dotel.exporter.otlp.endpoint=http://localhost:4317",
  "-Dotel.exporter.otlp.protocol=grpc",
  "-Dotel.traces.exporter=otlp",
  "-Dotel.logs.exporter=none",
  "-Dotel.metrics.exporter=none",
  "-Dfile.encoding=UTF-8",
  "-jar", 
  "app.jar"]