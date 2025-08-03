# Personal Finance Tracker

A comprehensive Spring Boot application for managing personal finances with JWT authentication, PostgreSQL database, and a modern web interface.

## 🚀 Features

- **User Authentication**: JWT-based authentication with user registration and login
- **Transaction Management**: Track income and expenses with categories
- **Category Management**: Create and manage custom categories for transactions
- **Financial Analytics**: View financial summaries and insights
- **Budget Tracking**: Set and monitor spending limits by category
- **Responsive UI**: Modern, mobile-friendly web interface
- **RESTful API**: Complete REST API with OpenAPI documentation
- **Security**: Role-based access control (USER/ADMIN)
- **Docker Support**: Containerized application with PostgreSQL

## 🛠️ Technology Stack

### Backend
- **Spring Boot 3.2.0** - Main framework
- **Spring Security** - Authentication and authorization
- **Spring Data JPA** - Database operations
- **PostgreSQL** - Database
- **JWT** - Token-based authentication
- **Gradle** - Build tool
- **OpenAPI/Swagger** - API documentation

### Frontend
- **HTML5/CSS3** - Modern, responsive design
- **Vanilla JavaScript** - Frontend functionality
- **Font Awesome** - Icons

### DevOps
- **Docker** - Containerization
- **Docker Compose** - Multi-container orchestration

## 📋 Prerequisites

- Java 17 or higher
- PostgreSQL 12 or higher (or Docker)
- Gradle 8.5 or higher (or use the included wrapper)

## 🚀 Quick Start

### Option 1: Using Docker (Recommended)

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd finance-tracker
   ```

2. **Start the application with Docker Compose**
   ```bash
   docker-compose up -d
   ```

3. **Access the application**
   - Web Interface: http://localhost:8080
   - API Documentation: http://localhost:8080/swagger-ui.html
   - Database: localhost:5432

### Option 2: Local Development

1. **Set up PostgreSQL**
   ```sql
   CREATE DATABASE finance_tracker;
   CREATE USER postgres WITH PASSWORD 'password';
   GRANT ALL PRIVILEGES ON DATABASE finance_tracker TO postgres;
   ```

2. **Update database configuration** (if needed)
   Edit `src/main/resources/application.yml`:
   ```yaml
   spring:
     datasource:
       url: jdbc:postgresql://localhost:5432/finance_tracker
       username: your_username
       password: your_password
   ```

3. **Run the application**
   ```bash
   ./gradlew bootRun
   ```

## 📚 API Documentation

### Authentication Endpoints

#### Register User
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "john_doe",
  "email": "john@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "password": "password123"
}
```

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "john_doe",
  "password": "password123"
}
```

### Transaction Endpoints

#### Get All Transactions
```http
GET /api/transactions
Authorization: Bearer <jwt_token>
```

#### Create Transaction
```http
POST /api/transactions
Authorization: Bearer <jwt_token>
Content-Type: application/json

{
  "description": "Grocery shopping",
  "amount": 75.50,
  "type": "EXPENSE",
  "category": { "id": 1 },
  "transactionDate": "2024-01-15",
  "notes": "Weekly groceries"
}
```

#### Get Financial Summary
```http
GET /api/transactions/summary?startDate=2024-01-01&endDate=2024-01-31
Authorization: Bearer <jwt_token>
```

### Category Endpoints

#### Get All Categories
```http
GET /api/categories
Authorization: Bearer <jwt_token>
```

#### Create Category
```http
POST /api/categories
Authorization: Bearer <jwt_token>
Content-Type: application/json

{
  "name": "Groceries",
  "type": "EXPENSE",
  "description": "Food and household items"
}
```

## 🗄️ Database Schema

### Users Table
```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Categories Table
```sql
CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    type VARCHAR(20) NOT NULL,
    user_id BIGINT REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Transactions Table
```sql
CREATE TABLE transactions (
    id BIGSERIAL PRIMARY KEY,
    description VARCHAR(200) NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    type VARCHAR(20) NOT NULL,
    transaction_date DATE NOT NULL,
    category_id BIGINT REFERENCES categories(id),
    user_id BIGINT REFERENCES users(id),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Budgets Table
```sql
CREATE TABLE budgets (
    id BIGSERIAL PRIMARY KEY,
    amount DECIMAL(10,2) NOT NULL,
    budget_month DATE NOT NULL,
    category_id BIGINT REFERENCES categories(id),
    user_id BIGINT REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## 🔧 Configuration

### Application Properties
Key configuration options in `application.yml`:

```yaml
# Database Configuration
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/finance_tracker
    username: postgres
    password: password

# JWT Configuration
jwt:
  secret: your-secret-key-here
  expiration: 86400000  # 24 hours

# Server Configuration
server:
  port: 8080
```

### Environment Variables
You can override configuration using environment variables:

```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/finance_tracker
export SPRING_DATASOURCE_USERNAME=postgres
export SPRING_DATASOURCE_PASSWORD=password
export JWT_SECRET=your-secret-key
```

## 🧪 Testing

### Run Tests
```bash
./gradlew test
```

### API Testing with cURL

#### Register a new user
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "firstName": "Test",
    "lastName": "User",
    "password": "password123"
  }'
```

#### Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'
```

#### Create a category (with JWT token)
```bash
curl -X POST http://localhost:8080/api/categories \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "name": "Groceries",
    "type": "EXPENSE",
    "description": "Food and household items"
  }'
```

## 🐳 Docker Commands

### Build and run with Docker Compose
```bash
# Start all services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop all services
docker-compose down

# Rebuild and start
docker-compose up --build -d
```

### Individual Docker commands
```bash
# Build the application
docker build -t finance-tracker .

# Run the application
docker run -p 8080:8080 finance-tracker

# Run PostgreSQL
docker run -d --name postgres \
  -e POSTGRES_DB=finance_tracker \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=password \
  -p 5432:5432 \
  postgres:15
```

## 📁 Project Structure

```
finance-tracker/
├── src/
│   ├── main/
│   │   ├── java/com/financetracker/
│   │   │   ├── config/          # Configuration classes
│   │   │   ├── controller/      # REST controllers
│   │   │   ├── dto/            # Data Transfer Objects
│   │   │   ├── entity/         # JPA entities
│   │   │   ├── exception/      # Exception handlers
│   │   │   ├── repository/     # Data repositories
│   │   │   ├── security/       # Security configuration
│   │   │   ├── service/        # Business logic
│   │   │   └── FinanceTrackerApplication.java
│   │   └── resources/
│   │       ├── static/         # Frontend files
│   │       │   ├── css/
│   │       │   ├── js/
│   │       │   └── index.html
│   │       └── application.yml
│   └── test/                   # Test files
├── build.gradle               # Gradle build configuration
├── docker-compose.yml         # Docker Compose configuration
├── Dockerfile                 # Docker configuration
└── README.md                  # This file
```

## 🔒 Security Features

- **JWT Authentication**: Secure token-based authentication
- **Password Encryption**: BCrypt password hashing
- **Role-based Access Control**: USER and ADMIN roles
- **Input Validation**: Comprehensive validation for all inputs
- **CORS Configuration**: Proper cross-origin resource sharing setup
- **SQL Injection Prevention**: JPA/Hibernate parameterized queries

## 🚀 Deployment

### Production Deployment

1. **Update configuration for production**
   ```yaml
   spring:
     profiles: production
     datasource:
       url: ${DATABASE_URL}
       username: ${DATABASE_USERNAME}
       password: ${DATABASE_PASSWORD}
   jwt:
     secret: ${JWT_SECRET}
   ```

2. **Build the application**
   ```bash
   ./gradlew build -x test
   ```

3. **Deploy with Docker**
   ```bash
   docker build -t finance-tracker:latest .
   docker run -d -p 8080:8080 finance-tracker:latest
   ```

### Cloud Deployment

The application can be deployed to various cloud platforms:

- **AWS**: Use ECS, EKS, or EC2
- **Google Cloud**: Use Cloud Run or GKE
- **Azure**: Use Azure Container Instances or AKS
- **Heroku**: Use Heroku Container Registry

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🆘 Support

If you encounter any issues or have questions:

1. Check the [API Documentation](http://localhost:8080/swagger-ui.html)
2. Review the logs for error messages
3. Ensure all prerequisites are met
4. Create an issue in the repository

## 🎯 Roadmap

- [ ] Budget alerts and notifications
- [ ] Export functionality (PDF, Excel)
- [ ] Advanced analytics and charts
- [ ] Mobile app (React Native)
- [ ] Multi-currency support
- [ ] Recurring transactions
- [ ] Bill reminders
- [ ] Investment tracking
- [ ] Tax reporting features

---

**Happy Finance Tracking! 💰📊**