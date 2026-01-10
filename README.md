🚗 RoadRescue

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen)
![Microservices](https://img.shields.io/badge/Architecture-Microservices-blue)
![Kafka](https://img.shields.io/badge/Kafka-Event--Driven-black)
![Redis](https://img.shields.io/badge/Redis-Caching-red)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Database-blue)
![License](https://img.shields.io/badge/License-MIT-green)

> **Help on Wheels, Anytime, Anywhere**

RoadRescue is a **mobile-first roadside assistance platform** that connects stranded vehicle owners with **verified nearby mechanics** in real time. Built using **Spring Boot microservices**, **Kafka**, and **cloud-native architecture**, it ensures fast response, transparent pricing, and secure payments.

---

## 📌 Table of Contents

- [Problem Statement](#-problem-statement)
- [Solution Overview](#-solution-overview)
- [Key Features](#-key-features)
- [System Architecture](#-system-architecture)
- [Microservices Overview](#-microservices-overview)
- [Business Flow](#-business-flow)
- [Edge Cases & Business Rules](#-edge-cases--business-rules)
- [Performance Optimizations](#-performance-optimizations)
- [Tech Stack](#-tech-stack)
- [Future Enhancements](#-future-enhancements)
- [License](#-license)

---

## ❗ Problem Statement

Vehicle breakdowns in unfamiliar or remote locations create serious challenges:

- No access to trusted mechanics
- Safety risks, especially at night or highways
- Unclear pricing and overcharging
- Long wait times with no ETA visibility
- Difficulty sharing exact location

---

## ✅ Solution Overview

RoadRescue solves this by providing:

- 📍 **Real-time mechanic discovery** using GPS
- 🔍 **Smart matching algorithm** based on distance, rating & specialization
- 💬 **Live tracking & notifications**
- 💳 **Transparent pricing & secure payments**
- ⭐ **Ratings & review system for trust**

---

## 🌟 Key Features

- Customer & Mechanic mobile apps
- Real-time location tracking
- Event-driven architecture with Kafka
- Secure JWT authentication
- Online payments with deposit holds
- Automated rating & badge system
- Admin dashboard for dispute handling

---

## 🏗 System Architecture

### High-Level Overview
```mermaid
flowchart TB

    subgraph Client_Layer
        CUST["Customer Mobile App (React Native)"]
        MECH["Mechanic Mobile App (React Native)"]
        ADMIN["Admin Web Portal (React.js)"]
    end

    subgraph Gateway
        API["API Gateway (Spring Cloud Gateway)"]
    end

    subgraph Core_Microservices
        USER["User Service"]
        REQ["Request Service"]
        MATCH["Matching Service"]
        LOC["Location Service"]
        PAY["Payment Service"]
        NOTIF["Notification Service"]
        RATE["Rating Service"]
        ANALYTICS["Analytics Service"]
        ADMIN_SVC["Admin Service"]
    end

    subgraph Messaging
        KAFKA[(Apache Kafka)]
    end

    subgraph Data_Layer
        PG[(PostgreSQL)]
        REDIS[(Redis GEO + Cache)]
    end

    subgraph External_Services
        MAPS["Google Maps API"]
        STRIPE["Stripe / Razorpay"]
        FCM["Firebase / Twilio"]
        S3["AWS S3"]
    end

    CUST --> API
    MECH --> API
    ADMIN --> API

    API --> USER
    API --> REQ
    API --> MATCH
    API --> LOC
    API --> PAY
    API --> NOTIF
    API --> RATE
    API --> ADMIN_SVC

    USER --> PG
    REQ --> PG
    PAY --> PG
    RATE --> PG

    LOC --> REDIS
    MATCH --> REDIS

    REQ --> KAFKA
    MATCH --> KAFKA
    PAY --> KAFKA
    LOC --> KAFKA
    RATE --> KAFKA

    LOC --> MAPS
    PAY --> STRIPE
    NOTIF --> FCM
    REQ --> S3
```
---

## 🧩 Microservices Overview

| Service | Port | Responsibility |
|-------|------|----------------|
| API Gateway | 8080 | Auth, routing, rate limiting |
| User Service | 8081 | Users, mechanics, profiles |
| Location Service | 8082 | GPS tracking, Redis GEO |
| Request Service | 8083 | Breakdown lifecycle |
| Matching Service | 8084 | Mechanic discovery & ranking |
| Payment Service | 8085 | Payments, deposits, payouts |
| Notification Service | 8086 | Push, SMS, WebSocket |
| Rating Service | 8087 | Reviews & ratings |
| Analytics Service | 8088 | Metrics & badges |
| Admin Service | 8089 | Disputes & moderation |

---

## 📊 Detailed Business Logic Flows

---

## 🔁 Flow 1: Customer Requests Breakdown Service

```mermaid
flowchart TB
    CUSTOMER["Customer Mobile App"]
    API["API Gateway"]
    USER["User Service"]
    REQ["Request Service"]
    KAFKA["Kafka: breakdown-requests"]
    MATCH["Matching Service"]
    NOTIF["Notification Service"]
    ANALYTICS["Analytics Service"]

    CUSTOMER -->|"1. Click Request Help (GPS captured)"| API
    API -->|"2. Verify JWT"| USER
    USER -->|"3. User authenticated"| REQ
    REQ -->|"4. Create breakdown request\nStatus: PENDING"| KAFKA

    KAFKA --> MATCH
    KAFKA --> NOTIF
    KAFKA --> ANALYTICS

    MATCH -->|"6. Find mechanics within 10km"| MATCH
    NOTIF -->|"7. Notify customer: Searching..."| CUSTOMER
```

## 🔁 Flow 2: Mechanic Matching & Assignment
```mermaid
flowchart TB
    KAFKA1["Kafka: breakdown-requests"]
    MATCH["Matching Service"]
    LOC["Location Service (Redis GEO)"]
    KAFKA2["Kafka: mechanic-notifications"]
    NOTIF["Notification Service"]
    MECH["Mechanic Mobile App"]
    API["API Gateway"]
    REQ["Request Service"]
    KAFKA3["Kafka: mechanic-assignments"]
    PAY["Payment Service"]

    KAFKA1 --> MATCH
    MATCH -->|"1. Query nearby mechanics"| LOC
    LOC -->|"2. GEORADIUS → M101, M102, M103"| MATCH
    MATCH -->|"3. Rank & select M101"| KAFKA2
    KAFKA2 --> NOTIF
    NOTIF -->|"New request nearby"| MECH
    MECH -->|"Accept"| API
    API --> REQ
    REQ -->|"Status: ASSIGNED"| KAFKA3
    KAFKA3 --> PAY
```

## 📍 Flow 3: Real-Time Location Tracking
```mermaid
flowchart TB
    MECH["Mechanic App"]
    API["API Gateway"]
    LOC["Location Service"]
    REDIS["Redis GEO"]
    KAFKA["Kafka: location-updates"]
    NOTIF["Notification Service"]
    CUSTOMER["Customer App"]

    MECH -->|"Send GPS every 10s"| API
    API --> LOC
    LOC -->|"GEOADD mechanic location"| REDIS
    LOC -->|"Publish ETA update"| KAFKA
    KAFKA --> NOTIF
    NOTIF -->|"Live tracking & ETA"| CUSTOMER
```

##  💳 Flow 4: Service Completion & Payment
```mermaid
flowchart TB
    MECH["Mechanic App"]
    API["API Gateway"]
    REQ["Request Service"]
    KAFKA1["Kafka: service-completion"]
    PAY["Payment Service"]
    GATEWAY["Stripe / Razorpay"]
    KAFKA2["Kafka: payments"]
    NOTIF["Notification Service"]
    RATE["Rating Service"]

    MECH -->|"Mark Complete"| API
    API --> REQ
    REQ -->|"Status: COMPLETED"| KAFKA1
    KAFKA1 --> PAY
    PAY -->|"Process payment"| GATEWAY
    GATEWAY -->|"Success"| PAY
    PAY --> KAFKA2
    KAFKA2 --> NOTIF
    KAFKA2 --> RATE
```

##  ⭐ Flow 5: Rating & Review
```mermaid
flowchart TB
    CUSTOMER["Customer App"]
    API["API Gateway"]
    RATE["Rating Service"]
    KAFKA["Kafka: reviews"]
    USER["User Service"]
    NOTIF["Notification Service"]
    ANALYTICS["Analytics Service"]

    CUSTOMER -->|"Submit rating"| API
    API --> RATE
    RATE -->|"Store review & recalc avg"| KAFKA
    KAFKA --> USER
    KAFKA --> NOTIF
    KAFKA --> ANALYTICS
```

---

## ⚠️ Edge Cases & Business Rules

### ⏱ Mechanic Timeout
- Reassign after 60 seconds
- Penalize acceptance rate
- Max 3 retries

### ❌ Customer Cancellation
| Time | Charge |
|----|-------|
| < 2 min | Free |
| 2–5 min | ₹50 |
| En route | ₹100 + distance |

### 🚫 No Mechanics Available
- Expand radius up to 50km
- Suggest towing service
- Log high-demand area

### 💳 Payment Failure
- Retry 3 times
- Mark `PAYMENT_PENDING`
- Auto support ticket

### ⚖️ Dispute Resolution
- Admin review
- Evidence verification
- Refund / payout / split decision

---

## 🚀 Performance Optimizations

### Redis Caching
- Mechanic locations (TTL: 30s)
- User profiles (TTL: 1h)
- Ratings cache (TTL: 5m)

### Database Indexing
```sql
CREATE INDEX idx_status_created 
ON breakdown_requests(status, created_at DESC);

CREATE INDEX idx_mechanic_requests 
ON breakdown_requests(mechanic_id, status);

CREATE INDEX idx_request_payment 
ON transactions(request_id);

