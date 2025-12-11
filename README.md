# Streaming Pipeline – Kafka → MongoDB (Indoor Air Quality)

This project implements a simple data streaming pipeline for indoor air quality monitoring:
- Indoor air quality data is read from a CSV file
- Each CSV row is sent as a message to a Kafka topic
- A Spring Boot consumer reads messages from Kafka, parses them and stores structured documents in MongoDB
- Duplicate messages are prevented via a unique index

---

## 1. Project Structure

```
streaming-pipeline/
├── src/main/java/.../
│   ├── StreamingPipelineApplication.java    # Spring Boot entry point
│   ├── kafka/
│   │   ├── AirQualityProducer.java          # CSV → Kafka producer
│   │   └── AirQualityConsumer.java          # Kafka → MongoDB consumer
│   └── mongo/
│       ├── AirQualityRecord.java            # MongoDB document model
│       └── AirQualityRecordRepository.java  # Spring Data repository
├── data/
│   └── IoT_Indoor_Air_Quality_Dataset.csv   # Input dataset
├── application.yml                           # Spring Boot configuration
├── docker-compose.yml                        # Multi-container orchestration
└── Dockerfile                                # Application container image
```

**Component Descriptions:**

- **StreamingPipelineApplication.java** – Starts the Spring Boot application and triggers CSV streaming on startup
- **AirQualityProducer.java** – Reads the CSV file line-by-line and produces Kafka messages to the configured topic
- **AirQualityConsumer.java** – Consumes messages from Kafka, parses CSV lines and saves `AirQualityRecord` documents to MongoDB with duplicate protection
- **AirQualityRecord.java** – MongoDB document model with `rawMessage` field marked as unique to prevent duplicates
- **AirQualityRecordRepository.java** – Spring Data MongoDB repository interface for database operations
- **application.yml** – Configures Kafka topic, consumer group ID and MongoDB connection parameters
- **docker-compose.yml** – Orchestrates Kafka (with Zookeeper), MongoDB and the application container
- **Dockerfile** – Builds the Docker image for the Spring Boot application with embedded JDK and Maven

---

## 2. Prerequisites

- Docker and Docker Compose installed
- Internet connection for pulling Docker images

Everything else (JDK, Maven) is handled inside the Docker image.

---

## 3. How to Run the Pipeline

### 3.1 Clone the Repository

```bash
git clone <REPOSITORY_URL>
cd <REPOSITORY_FOLDER>
```

### 3.2 Build and Start All Containers

From the project root (where `docker-compose.yml` is located):

```bash
docker compose up --build
```

**What happens:**
- Docker builds the application image using the `Dockerfile`
- Zookeeper, Kafka, MongoDB and the Spring Boot app containers are started
- On startup, the application reads `data/IoT_Indoor_Air_Quality_Dataset.csv`, sends all rows to Kafka and writes parsed documents to MongoDB

### 3.3 Stop the Containers

After 1-2 minutes of runtime (or when processing completes):

```bash
docker compose down
```

---

## 4. Verifying the Result in MongoDB

### 4.1 Open MongoDB Shell

Connect to the running MongoDB container:

```bash
docker exec -it mongo mongosh
```

### 4.2 Inspect the Collection

Switch to the database and query the collection:

```javascript
use airquality
db.air_quality_events.countDocuments()
db.air_quality_events.find().limit(5)
```

**Expected Output:**

```javascript
{
  _id: ObjectId("..."),
  rawMessage: "18-02-2024 08:00,21.75,...,Open",
  timestamp: "18-02-2024 08:00",
  temperature: 21.75,
  humidity: 63.11,
  co2: 989.74,
  pm25: 31.17,
  pm10: 89.66,
  tvoc: 226.83,
  co: 2.84,
  lightIntensity: 646.86,
  motionDetected: true,
  occupancyCount: 49,
  ventilationStatus: "Open",
  receivedAt: ISODate("...")
}
```

---

## 5. Duplicate Prevention

The `AirQualityRecord` model uses a unique index on the `rawMessage` field:

```java
@Indexed(unique = true)
private String rawMessage;
```

**How it works:**
- When the CSV is streamed multiple times, Kafka may receive duplicate messages
- MongoDB rejects duplicate inserts based on the `rawMessage` unique constraint
- The application handles `DuplicateKeyException` gracefully and continues processing subsequent messages
- This ensures idempotent message processing even with Kafka message replay

---

## 6. Documentation

A separate PDF document describes:
- System architecture and component interactions
- Design decisions and technology choices
- Reflection on what worked well
- Known limitations
- Possible improvements and future enhancements

---

## 7. Technology Stack

- **Spring Boot** – Application framework
- **Apache Kafka** – Message streaming platform
- **MongoDB** – Document database
- **Docker & Docker Compose** – Containerization and orchestration
- **Maven** – Dependency management and build tool
