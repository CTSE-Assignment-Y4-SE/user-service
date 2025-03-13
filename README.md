# garageBySapu_user-service

## 🚀 Overview
**garageBySapu_user-service** is a microservice responsible for handling **User Management, Authentication, and Authorization** within the Garage application. It provides features like user registration, login, role-based access control, OTP verification, email notifications, and JWT-based authentication.

## 🛠️ Tech Stack
- **Spring Boot** - Backend Framework
- **Java** - Core Language
- **Microservices Architecture**
- **Docker** - Containerization
- **SQL Database** - Relational Data Storage
- **Kafka** - Event-driven communication
- **JWT (JSON Web Token)** - Secure Authentication
- **OTP & Email** - Secure User Verification

## 📌 Features
- 🔐 **User Authentication & Authorization** (JWT-based security)
- 👤 **User Management** (Registration, Login, Profile Management)
- 🔄 **Role-based Access Control (RBAC)**
- 📧 **Email & OTP Verification** for enhanced security
- 🔄 **Kafka Integration** for event-driven processing
- 📦 **Docker Support** for easy deployment

## ⚙️ Installation & Setup
### Prerequisites
- **Java 17+**
- **Docker** (for containerized setup)
- **PostgreSQL / MySQL** (SQL database)
- **Kafka** (for event streaming)
- **Gradle** or **Maven** (for dependency management)

### 🔹 Clone the Repository
```sh
 git clone https://github.com/CTSE-Assignment-Y4-SE/garageBySapu_user-service.git
 cd garageBySapu_user-service
```

### 🔹 Setup Database
Update `application.yml` with your SQL database configurations:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/garage_db
    username: your_db_username
    password: your_db_password
```

### 🔹 Running Locally
```sh
./gradlew bootRun
```

### 🔹 Running with Docker
```sh
docker build -t garage-user-service .
docker run -p 8080:8080 garage-user-service
```

## 🔑 API Endpoints
### 📌 Authentication
| Method | Endpoint | Description |
|--------|---------|-------------|
| POST   | `/auth/register` | Register new user |
| POST   | `/auth/login` | Login and get JWT token |
| POST   | `/auth/verify-otp` | Verify OTP for authentication |

### 📌 User Management
| Method | Endpoint | Description |
|--------|---------|-------------|
| GET    | `/users/{id}` | Get user by ID |
| PUT    | `/users/{id}` | Update user details |
| DELETE | `/users/{id}` | Delete user |

## 🏗️ Microservices Communication
- This service communicates with other microservices using **Kafka topics**.
- Events like `USER_CREATED`, `USER_UPDATED`, and `USER_DELETED` are published to Kafka.

## 🔐 Security
- Uses **JWT for Authentication**.
- Implements **Role-based Access Control (RBAC)**.
- Enforces **password hashing and secure storage**.
- Supports **OTP-based two-factor authentication**.

## 📩 Contact & Contribution
Want to contribute? Feel free to **fork and submit a pull request**.

For any issues, please open a ticket in the **Issues** section.

---

⭐ **garageBySapu_user-service** is part of the **Garage Application Microservices** ecosystem!

* Developed by Harith Vithanage (harith2001)