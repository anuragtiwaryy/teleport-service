# Teleport Service
# 🚀 Scalable Tracking Number Generator API

## 📌 Overview
This project is a **RESTful API** that generates **unique tracking numbers** for parcels which first 5 Character are (Source+Destination+WeightGroup+followedByToken.) :  MYIDL2JVG1EBNG00  
It is built using **Spring Boot**, leverages **Redis (AWS ElastiCache)** for scalability and rate limiting, and exposes **Spring Actuator** endpoints for monitoring.

Key Features:
- **Generate unique tracking numbers** (`^[A-Z0-9]{1,16}$`)
- **Rate limiting:** max **100 requests per customer per minute**
- **Horizontal scalability** with Redis as a distributed cache
- **Spring Actuator** for health checks & metrics

---

## 🏗️ Tech Stack
- **Java 17+**
- **Spring Boot 3+**
- **Spring Web**
- **Spring Actuator**
- **Spring Data Redis**
- **AWS EC2** (application host)
- **AWS ElastiCache (Redis)** (distributed caching & rate limiting)
- **Maven/Gradle**

---

## ⚙️ Setup Instructions

### 1. Clone the repo
```bash
git clone https://github.com/<your-repo>/tracking-number-api.git
cd tracking-number-api
```

### 2. Configure application
Update your `application.properties` or `application.yml`:
```properties
server.port=8080

spring.redis.host=<your-elasticache-primary-endpoint>
spring.redis.port=6379
management.endpoints.web.exposure.include=*
```

### 3. Build & Run
```bash
./mvnw clean install
./mvnw spring-boot:run
```

App will be available at:
```
http://ec2-16-171-24-203.eu-north-1.compute.amazonaws.com:8080
```

---

## 📡 API Endpoints

### Generate Tracking Number
```http
GET /next-tracking-number
```

#### Query Params
- `origin_country_id` (e.g., MY)
- `destination_country_id` (e.g., ID)
- `weight` (e.g., 1.234)
- `created_at` (e.g., 2018-11-20T19:29:32+08:00)
- `customer_id` (UUID)
- `customer_name` (string)
- `customer_slug` (string)

#### Example
```bash
curl "http://ec2-16-171-24-203.eu-north-1.compute.amazonaws.com:8080/teleport-service/next-tracking-number?origin_country_id=MY&destination_country_id=ID&weight=1.234&created_at=2018-11-20T19:29:32+08:00&customer_id=de619854-b59b-425e-9db4-943979e1bd49&customer_name=RedBox Logistics&customer_slug=redbox-logistics"
```

#### Sample Response
```json
{
  "status": "OK",
  "data": {
    "trackingNumber": "MYIDL2JVH7KMH000",
    "createdAt": "2025-09-18T19:21:10.307291708+08:00"
  },
  "message": "SUCCESS",
  "statusCode": 200
}
```

---

## ⏱️ Rate Limiting
- Each **customer_id** is limited to **100 tracking number requests per minute**.
- Implemented using Redis `INCR` with TTL.

---

## 🔍 Monitoring (Actuator)
Actuator endpoints:
- `/actuator/health` → health check
- `/actuator/metrics` → metrics
- `/actuator/prometheus` → (if enabled) for Prometheus scraping

---

## 🚀 Deployment
- **EC2 instance** hosts the Spring Boot application.
- **ElastiCache (Redis)** is used for:
    - Distributed lock / uniqueness
    - Rate limiting
- Security group:
    - EC2 SG allowed to access Redis SG on port **6379**
    - API exposed on EC2 public IP:8080
    - command to run app :java -jar Teleport-Service.jar

---


-**Note: You can run this app locally by just downloading redis and start redis-server.exe**
