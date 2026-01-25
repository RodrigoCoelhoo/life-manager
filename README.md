<p align="center">
  <img src="docs/images/banner.png" alt="LifeManager banner" />
</p>

<p>
  <img src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white" alt="Java Badge Shield" />
  <img src="https://img.shields.io/badge/Spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white" alt="Spring Badge Shield" />
  <img src="https://img.shields.io/badge/Redis-D9281A?style=for-the-badge&logo=redis&logoColor=white" alt="Redis Badge Shield" />
  <img src="https://img.shields.io/badge/React-20232A?style=for-the-badge&logo=react&logoColor=61DAFB" alt="React Badge Shield" />
  <img src="https://img.shields.io/badge/TypeScript-007ACC?style=for-the-badge&logo=typescript&logoColor=white" alt="Typescript Badge Shield" />
  <img src="https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white" alt="PostgreSQL Badge Shield" />
  <img src="https://img.shields.io/badge/Tailwind_CSS-38B2AC?style=for-the-badge&logo=tailwind-css&logoColor=white" alt="TailwindCss Badge Shield" />
  <img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white" alt="Docker Badge Shield" />
</p>

https://github.com/iuricode/readme-template/tree/main/avancado

> [!NOTE]  
> **Life Manager** is an all-in-one personal management app designed to help users organize and track the most important areas of their life: **Finances, Nutrition, and Training**.

## 🎯 Project Purpose

> [!WARNING]
> Life Manager was developed as a learning-focused portfolio project with the goal of consolidating and expanding practical knowledge in full-stack development.

### The project was specifically used to:
- Learn React and modern frontend development from scratch
- Learn UI responsiveness and CSS properties (TailwindCSS)
- Learn Spring Boot and backend application structure
- Understand and apply authentication concepts
- Practice the use of DTOs, service layers, and clean application structure
- Solidify core concepts such as separation of concerns and maintainability

## 🚀 Features

### 🥗 Nutrition Management
- Track meals and nutritional data
- Organize daily and weekly intake

### 🏋️ Training Management
- Manage workouts and training routines
- Track progress over time

### 💰 Finance Management
- Record expenses and income
- Categorize financial activity

## 🏗️ Architecture Overview
- **Frontend:** Single Page Application built with React and TypeScript
- **Backend:** RESTful API developed with Spring Boot
- **Authentication:** JWT with access and refresh tokens
- **Database:** PostgreSQL with schema versioning via Flyway
- **Caching:** Redis integrated using Spring Cache abstraction
- **Infrastructure:** Docker for local development

## 🛠️ Getting Started

### Docker

> [!IMPORTANT]
> Docker Desktop must be installed on your device

1. Go to the project root directory:
```bash
cd .../life-manager
```

2. Build and start docker containers:
```bash
docker compose -f docker-compose.dev.yml up --build -d
```

#### Stopping containers
```bash
docker compose -f docker-compose.dev.yml down
```

---

### Locally

#### Frontend

> [!IMPORTANT]
> Node.js (LTS version recommended, with npm) must be installed on your device

1. Go to the project root directory:
```bash
cd .../life-manager/frontend
```

2. Install dependencies:
```bash
npm install
```

3. Start the development server:
```bash
npm run dev
```

4. Go to the web page:

<a href="http://localhost:3000/">Life Manager - Web page</a>

#### Backend

> [!IMPORTANT]
> The following must be installed on your device:
> - Java JDK 17+  
> - Maven or Intellij IDEA
> - PostgreSQL  
> - Redis

1. Open ``application.properties`` file in an editor:
`.../life-manager/backend/src/main/resources/application.properties`

2. Update ``application.properties`` with your Redis and PostgreSQL information:
```properties
spring.application.name=lifemanager

# ===============================
# Database (PostgreSQL - Local)
# ===============================
spring.datasource.url=jdbc:postgresql://localhost:5432/lifemanager
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.datasource.driver-class-name=org.postgresql.Driver

# ===============================
# JPA / Hibernate
# ===============================
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
spring.jpa.open-in-view=false

# ===============================
# Security
# ===============================
# Uses JWT_SECRET env variable if present, otherwise fallback
api.security.token.secret=${JWT_SECRET:my-secret-key}

# ===============================
# Error handling
# ===============================
server.error.include-message=always
server.error.include-stacktrace=always

# ===============================
# Logging
# ===============================
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql=TRACE
logging.level.org.springframework.transaction=TRACE

# ===============================
# Redis (Local)
# ===============================
spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.cache.type=redis
spring.cache.redis.time-to-live=600000
spring.cache.redis.cache-null-values=false
```

3. Go to the backend project root directory:
```bash
cd .../life-manager/backend
```

4. Start the Spring Boot application:

**Option 1 – Using Maven CLI:**

Run this command from the backend project root (where `pom.xml` is):

```bash
cd .../life-manager/backend
mvn spring-boot:run
```

**Option 2 – Using IntelliJ IDEA:**
1. Open the backend folder as a project in IntelliJ.
2. Locate LifeManagerApplication.java in src/main/java/....
3. Right-click the file and select ``Run 'LifeManagerApplication'``.
4. The application will start, and the backend API will be available at http://localhost:8080.