# GymLog — Personal Workout & Nutrition Tracker 💪

A full-stack fitness tracking application built with **Spring Boot 3** and **React + Tailwind CSS**.

## 🛠 Tech Stack

### Backend
- **Java 21** + **Spring Boot 3.3.5**
- **Spring Security** + **JWT Authentication**
- **Spring Data JPA** + **Hibernate**
- **MySQL** Database
- **Swagger/OpenAPI** Documentation
- **Maven** Build Tool
- **Lombok** for boilerplate reduction

### Frontend
- **React 19** (Vite)
- **Tailwind CSS 3** with custom design system
- **React Router 7** for navigation
- **Recharts** for analytics visualizations
- **Axios** with interceptors for API communication
- **React Hot Toast** for notifications

## ✨ Features

### 🔐 Authentication
- JWT-based stateless authentication
- User registration & login
- Role-based access control (USER / ADMIN)
- Change password functionality

### 🏋️ Workout Tracking
- Create and log workout sessions
- Track sets, reps, and weights per exercise
- Workout history with pagination
- Workout status tracking (Planned / In Progress / Completed)
- Automatic volume calculation

### 📚 Exercise Library
- 50+ pre-seeded exercises across categories
- Search and filter by muscle group, equipment, difficulty
- Category-based browsing
- Custom exercise support

### 🥗 Nutrition Tracking
- Daily calorie & macro tracking (Protein, Carbs, Fat)
- Food log with meal types (Breakfast, Lunch, Dinner, Snack)
- Daily nutrition summary with calorie target percentage
- Date-based food history

### 📏 Body Progress
- Track weight, body fat %, and body measurements
- Weight trend visualization chart
- Measurement history table

### 🎯 Goals
- Set fitness goals (Weight, Strength, Nutrition, Workout Frequency)
- Progress tracking with visual progress bars
- Goal status management (Active / Completed / Abandoned)

### 📊 Analytics Dashboard
- Weight progression chart
- Workout volume trends
- Calorie intake trends
- Strength progression per exercise
- Dashboard with key stats overview

### 👤 Admin Panel
- Platform-wide statistics
- User management (activate/deactivate)
- Role-based admin access

## 🚀 Getting Started

### Prerequisites
- **Java 21** (JDK)
- **MySQL 8.x**
- **Node.js 18+** and npm

### Database Setup
```sql
CREATE DATABASE gymlog_db;
CREATE USER 'gymlog'@'localhost' IDENTIFIED BY 'gymlog123';
GRANT ALL PRIVILEGES ON gymlog_db.* TO 'gymlog'@'localhost';
FLUSH PRIVILEGES;
```

### Backend Setup
```bash
cd gymlog-backend
./mvnw.cmd spring-boot:run   # Windows
# OR
./mvnw spring-boot:run       # Mac/Linux
```
The backend will start on `http://localhost:8080`

### Frontend Setup
```bash
cd gymlog-frontend
npm install
npm run dev
```
The frontend will start on `http://localhost:5173`

### Default Accounts
| Role  | Email              | Password   |
|-------|--------------------|------------|
| User  | demo@gymlog.com    | Demo@123   |
| Admin | admin@gymlog.com   | Admin@123  |

## 📁 Project Structure

```
GymLog/
├── gymlog-backend/          # Spring Boot API
│   └── src/main/java/com/gymlog/
│       ├── config/          # Security, CORS, Swagger, DataInitializer
│       ├── controller/      # REST API endpoints
│       ├── dto/             # Request/Response DTOs
│       ├── entity/          # JPA entities
│       ├── enums/           # Enumerations
│       ├── exception/       # Global exception handling
│       ├── repository/      # Spring Data JPA repositories
│       ├── security/        # JWT filter, provider, entry point
│       └── service/impl/    # Business logic layer
│
├── gymlog-frontend/         # React SPA
│   └── src/
│       ├── api/             # Axios instance & API service definitions
│       ├── components/      # Sidebar, TopBar
│       ├── context/         # AuthContext, ThemeContext
│       ├── layouts/         # DashboardLayout
│       └── pages/           # All page components
│
└── README.md
```

## 📄 API Documentation
Once the backend is running, visit:
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs

## 📝 License
This project is for educational and portfolio purposes.
