# SkillShare Portal

A full-stack Spring Boot web application where users can register/login and share skill-based content.

## Features

- 🔐 **JWT-based Authentication** with Spring Security
- 👥 **User Management** with role-based access (USER, ADMIN)
- 📚 **Skill Sharing** - Users can add and view skills
- 🎨 **Modern UI** with responsive design
- 📊 **MySQL Database** for data persistence
- 📖 **OpenAPI/Swagger** documentation
- 🐳 **Docker Support** for easy deployment

## Tech Stack

### Backend
- **Spring Boot 3.2.0** - Main framework
- **Spring Security** - Authentication and authorization
- **Spring Data JPA** - Database operations
- **MySQL** - Database
- **JWT** - Token-based authentication
- **Gradle** - Build tool
- **OpenAPI/Swagger** - API documentation

### Frontend
- **HTML5** - Structure
- **CSS3** - Styling with modern design
- **Vanilla JavaScript** - Client-side functionality

## Prerequisites

- Java 17 or higher
- MySQL 8.0 or higher
- Gradle (or use Gradle wrapper)

## Quick Start

### Option 1: Using Docker (Recommended)

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd skillshare-portal
   ```

2. **Run with Docker Compose**
   ```bash
   docker-compose up -d
   ```

3. **Access the application**
   - Frontend: http://localhost:8080
   - Swagger UI: http://localhost:8080/swagger-ui/index.html
   - API Docs: http://localhost:8080/v3/api-docs

### Option 2: Local Development

1. **Set up MySQL**
   ```sql
   CREATE DATABASE classdb;
   CREATE USER 'root'@'localhost' IDENTIFIED BY 'password';
   GRANT ALL PRIVILEGES ON classdb.* TO 'root'@'localhost';
   FLUSH PRIVILEGES;
   ```

2. **Run the application**
   ```bash
   ./gradlew bootRun
   ```

3. **Access the application**
   - Frontend: http://localhost:8080
   - Swagger UI: http://localhost:8080/swagger-ui/index.html

## API Endpoints

### Authentication
- `POST /auth/register` - Register a new user
- `POST /auth/login` - Login user

### Skills
- `POST /skills/add` - Add a new skill (requires authentication)
- `GET /skills/all` - Get all public skills
- `GET /skills/my` - Get user's skills (requires authentication)
- `GET /skills/category/{category}` - Get skills by category

## Database Schema

### Users Table
```sql
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('USER', 'ADMIN') NOT NULL DEFAULT 'USER',
    enabled BOOLEAN NOT NULL DEFAULT TRUE
);
```

### Skills Table
```sql
CREATE TABLE skills (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    category VARCHAR(100) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_id BIGINT NOT NULL,
    is_public BOOLEAN NOT NULL DEFAULT TRUE,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

## Testing with Postman/Curl

### 1. Register a new user
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "email": "john@example.com",
    "password": "password123"
  }'
```

### 2. Login
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "password": "password123"
  }'
```

### 3. Add a skill (use token from login response)
```bash
curl -X POST http://localhost:8080/skills/add \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "name": "Java Programming",
    "description": "Expert in Java development with Spring Boot",
    "category": "Programming",
    "isPublic": true
  }'
```

### 4. Get all public skills
```bash
curl -X GET http://localhost:8080/skills/all
```

## Project Structure

```
src/
├── main/
│   ├── java/com/skillshare/
│   │   ├── config/           # Configuration classes
│   │   ├── controller/       # REST controllers
│   │   ├── dto/             # Data Transfer Objects
│   │   ├── entity/          # JPA entities
│   │   ├── exception/       # Global exception handler
│   │   ├── repository/      # Data repositories
│   │   ├── security/        # Security configuration
│   │   ├── service/         # Business logic
│   │   └── SkillShareApplication.java
│   └── resources/
│       ├── static/          # Frontend files
│       │   ├── css/
│       │   ├── dashboard.html
│       │   └── index.html
│       └── application.yml  # Application configuration
├── test/                    # Test files
build.gradle                 # Gradle build file
Dockerfile                   # Docker configuration
docker-compose.yml          # Docker Compose setup
README.md                   # This file
```

## Configuration

The application uses the following configuration in `application.yml`:

- **Database**: MySQL with credentials (root/password)
- **JWT**: 24-hour expiration with secure secret
- **Server**: Port 8080
- **CORS**: Enabled for frontend integration

## Security Features

- **JWT Authentication**: Stateless token-based authentication
- **Password Encryption**: BCrypt password hashing
- **Role-based Access**: USER and ADMIN roles
- **Input Validation**: Comprehensive validation with custom error messages
- **CORS Configuration**: Secure cross-origin resource sharing

## Frontend Features

- **Responsive Design**: Works on desktop and mobile
- **Modern UI**: Beautiful gradient design with animations
- **Tab-based Forms**: Easy switching between login and register
- **Real-time Feedback**: Success/error messages
- **Dashboard**: Skill management interface

## Troubleshooting

### Common Issues

1. **Database Connection Error**
   - Ensure MySQL is running
   - Check database credentials in `application.yml`
   - Verify database `classdb` exists

2. **Port Already in Use**
   - Change port in `application.yml` or stop conflicting service
   - Default port is 8080

3. **JWT Token Issues**
   - Check token expiration (24 hours by default)
   - Ensure proper Authorization header format: `Bearer <token>`

### Logs

Check application logs for detailed error information:
```bash
# Docker
docker-compose logs skillshare-app

# Local
./gradlew bootRun
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## License

This project is licensed under the MIT License.

## Support

For support and questions, please open an issue in the repository.