# === Build stage ===
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# copy project
COPY . .

# build jar
RUN ./mvnw -q -B package -DskipTests

# === Runtime stage ===
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# copy fat jar
COPY --from=build /app/target/streaming-pipeline-*.jar app.jar

# copy CSV so the path data/IoT_Indoor_Air_Quality_Dataset.csv still works
COPY data ./data

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
