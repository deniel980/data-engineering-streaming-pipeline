# Streaming Pipeline – Kafka → MongoDB (Indoor Air Quality)

This project implements a data streaming pipeline for monitoring of indoor air quality:
- Air quality data is ingested from a CSV file
- Each CSV row is sent as a message to a topic in Kafka
- Spring Boot consumer reads messages from Kafka, parses and stores them as documents in MongoDB
- Data duplication prevention is implemented via a unique index

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
├── application.yml                          # Spring Boot configuration
├── docker-compose.yml                       # Multi-container orchestration
└── Dockerfile                               # Application container image
```

**Component Descriptions:**

- **StreamingPipelineApplication.java** – Starts the Spring Boot application and CSV streaming on startup
- **AirQualityProducer.java** – Reads the CSV file by line and produces Kafka messages to the defined topic
- **AirQualityConsumer.java** – Consumes messages from Kafka, parses CSV datasets and saves them as `AirQualityRecord` documents to MongoDB with duplicate protection
- **AirQualityRecord.java** – MongoDB document model with `rawMessage` field marked as unique for data duplication protection
- **AirQualityRecordRepository.java** – Spring Data MongoDB repository for database related operations
- **application.yml** – Configures Kafka topic, consumer group ID and connection parameters for MongoDB
- **docker-compose.yml** – Orchestrates Kafka, MongoDB Instance and the SpringBoot application container
- **Dockerfile** – Builds the Docker image for the Spring Boot service with JDK and Maven embedded

---

## 2. Prerequisites

- installed Docker and Docker Compose
- Internet connection to pull Docker images

Everything else should be handled inside the Docker image.

---

## 3. How to Run the Pipeline

### 3.1 Clone the Repository

```bash
git clone <REPOSITORY_URL>
cd <REPOSITORY_FOLDER>
```

### 3.2 Build and Start All Containers

Execute from the project root (where `docker-compose.yml` is located) the following:

```bash
docker compose up --build
```

**What happens:**
- Docker will build the application image using existing `Dockerfile`
- Zookeeper, MongoDB, Kafka and the Spring Boot application containers are started
- On startup, the application processes `data/IoT_Indoor_Air_Quality_Dataset.csv`, sends datasets to Kafka and writes them as parsed documents to MongoDB
- the stream speed is throttled to 10 messages/second to stabilize the process across different hardware resources and better vizualize the streaming behavior




---

## 4. Verifying the Result in MongoDB (let the service run for 30-90 seconds)

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

### 4.3 Stop and remove the Containers

```bash
docker compose down
```



---

## 5. Duplicate Prevention

The `AirQualityRecord` model uses a unique index on the `rawMessage` field for that purpose:

```java
@Indexed(unique = true)
private String rawMessage;
```

**How it works:**
- When the same CSV line is streamed multiple times, Kafka will receive duplicate messages
- MongoDB will reject duplicate inserts based on the `rawMessage` unique index constraint
- The application handles `DuplicateKeyException` gracefully and continues processing subsequent messages
- This ensures idempotent processing of messages even with Kafka message replay

---

## 6. Documentation

A separate PDF document describes the following:
- System architecture and its component interactions
- Design decisions and technology choices
- Reflection on what worked well
- Known limitations
- Possible improvements and future enhancements

---

## 7. Technology Stack Overview

- **Spring Boot** – Application framework
- **Apache Kafka** – Message streaming platform
- **MongoDB** – Document-oriented NoSQL database
- **Docker & Docker Compose** – Containerization and orchestration
- **Maven** – Dependency management and build tool
