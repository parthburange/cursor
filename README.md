# Personal Finance Tracker

A comprehensive Spring Boot application for personal finance management with JWT authentication, PostgreSQL database, and a modern web interface.

## 🚀 Features

- **User Authentication**: Secure JWT-based authentication with user registration and login
- **Transaction Management**: Track income and expenses with detailed categorization
- **Category Management**: Create custom categories for organizing transactions
- **Budget Tracking**: Set and monitor spending budgets by category
- **Financial Analytics**: View income, expense, and net amount summaries
- **Modern UI**: Responsive web interface with beautiful design
- **RESTful API**: Complete REST API with Swagger documentation
- **Security**: Spring Security with role-based access control
- **Database**: PostgreSQL with JPA/Hibernate for data persistence

## 🛠️ Technology Stack

### Backend
- **Spring Boot 3.2.0** - Main framework
- **Spring Security** - Authentication and authorization
- **Spring Data JPA** - Database operations
- **PostgreSQL** - Database
- **JWT** - Token-based authentication
- **Gradle** - Build tool
- **Swagger/OpenAPI** - API documentation

### Frontend
- **HTML5** - Structure
- **CSS3** - Styling with modern design
- **JavaScript (ES6+)** - Client-side functionality
- **Font Awesome** - Icons

### DevOps
- **Docker** - Containerization
- **Docker Compose** - Multi-container orchestration

## 📋 Prerequisites

- Java 17 or higher
- Gradle 7.6+ (or use Gradle wrapper)
- PostgreSQL 12+ (or use Docker)
- Docker & Docker Compose (optional)

## 🚀 Quick Start

### Option 1: Using Docker Compose (Recommended)

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd finance-tracker
   ```

2. **Run with Docker Compose**
   ```bash
   docker-compose up -d
   ```

3. **Access the application**
   - Web Interface: http://localhost:8080
   - API Documentation: http://localhost:8080/swagger-ui.html
   - Database: localhost:5432

### Option 2: Local Development

1. **Set up PostgreSQL**
   ```bash
   # Create database
   createdb finance_tracker
   
   # Or using Docker
   docker run --name postgres -e POSTGRES_DB=finance_tracker -e POSTGRES_PASSWORD=password -p 5432:5432 -d postgres:15-alpine
   ```

2. **Configure application**
   - Update `src/main/resources/application.yml` with your database credentials

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
  "password": "password123",
  "firstName": "John",
  "lastName": "Doe",
  "monthlyBudget": 5000.00,
  "currency": "USD"
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

#### Create Transaction
```http
POST /api/transactions
Authorization: Bearer <jwt-token>
Content-Type: application/json

{
  "description": "Grocery shopping",
  "amount": 150.50,
  "type": "EXPENSE",
  "categoryId": 1,
  "transactionDate": "2024-01-15",
  "paymentMethod": "Credit Card",
  "notes": "Weekly groceries"
}
```

#### Get All Transactions
```http
GET /api/transactions
Authorization: Bearer <jwt-token>
```

#### Get Financial Summary
```http
GET /api/transactions/summary?startDate=2024-01-01&endDate=2024-01-31
Authorization: Bearer <jwt-token>
```

### Category Endpoints

#### Create Category
```http
POST /api/categories
Authorization: Bearer <jwt-token>
Content-Type: application/json

{
  "name": "Groceries",
  "type": "EXPENSE",
  "description": "Food and household items",
  "colorHex": "#ff6b6b"
}
```

#### Get All Categories
```http
GET /api/categories
Authorization: Bearer <jwt-token>
```

### Budget Endpoints

#### Create Budget
```http
POST /api/budgets
Authorization: Bearer <jwt-token>
Content-Type: application/json

{
  "budgetAmount": 500.00,
  "startDate": "2024-01-01",
  "endDate": "2024-01-31",
  "categoryId": 1,
  "description": "Monthly grocery budget"
}
```

## 🗄️ Database Schema

### Users Table
- `id` - Primary key
- `username` - Unique username
- `email` - Unique email
- `password` - Encrypted password
- `first_name`, `last_name` - User details
- `role` - USER/ADMIN
- `monthly_budget` - Optional monthly budget
- `currency` - Default currency (USD)
- `created_at`, `updated_at` - Timestamps

### Categories Table
- `id` - Primary key
- `name` - Category name
- `type` - INCOME/EXPENSE
- `description` - Optional description
- `color_hex` - Color for UI
- `user_id` - Foreign key to users
- `created_at`, `updated_at` - Timestamps

### Transactions Table
- `id` - Primary key
- `description` - Transaction description
- `amount` - Transaction amount
- `type` - INCOME/EXPENSE
- `transaction_date` - Date of transaction
- `category_id` - Foreign key to categories
- `user_id` - Foreign key to users
- `payment_method` - Optional payment method
- `notes` - Optional notes
- `is_recurring` - Boolean for recurring transactions
- `recurrence_type` - DAILY/WEEKLY/MONTHLY/YEARLY
- `created_at`, `updated_at` - Timestamps

### Budgets Table
- `id` - Primary key
- `budget_amount` - Budget amount
- `start_date`, `end_date` - Budget period
- `category_id` - Optional foreign key to categories
- `user_id` - Foreign key to users
- `description` - Optional description
- `is_active` - Boolean for active status
- `created_at`, `updated_at` - Timestamps

## 🔧 Configuration

### Application Properties
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/finance_tracker
    username: postgres
    password: password
  
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

jwt:
  secret: your-256-bit-secret-key-here-make-it-long-and-secure
  expiration: 86400000 # 24 hours
```

### Environment Variables
- `SPRING_DATASOURCE_URL` - Database connection URL
- `SPRING_DATASOURCE_USERNAME` - Database username
- `SPRING_DATASOURCE_PASSWORD` - Database password
- `JWT_SECRET` - JWT signing secret
- `JWT_EXPIRATION` - JWT expiration time in milliseconds

## 🧪 Testing

### Run Tests
```bash
./gradlew test
```

### API Testing with Postman

1. **Import the collection** (create a Postman collection with the endpoints)
2. **Set up environment variables**:
   - `baseUrl`: http://localhost:8080/api
   - `token`: (will be set after login)

3. **Test Flow**:
   - Register a new user
   - Login to get JWT token
   - Create categories
   - Add transactions
   - View financial summary

### Example Test Data

```json
// Register
{
  "username": "testuser",
  "email": "test@example.com",
  "password": "password123",
  "firstName": "Test",
  "lastName": "User",
  "monthlyBudget": 3000.00
}

// Create Income Category
{
  "name": "Salary",
  "type": "INCOME",
  "description": "Monthly salary",
  "colorHex": "#27ae60"
}

// Create Expense Category
{
  "name": "Food",
  "type": "EXPENSE",
  "description": "Food and dining",
  "colorHex": "#e74c3c"
}

// Add Income Transaction
{
  "description": "Monthly Salary",
  "amount": 5000.00,
  "type": "INCOME",
  "categoryId": 1,
  "transactionDate": "2024-01-15",
  "paymentMethod": "Direct Deposit"
}

// Add Expense Transaction
{
  "description": "Grocery Shopping",
  "amount": 150.50,
  "type": "EXPENSE",
  "categoryId": 2,
  "transactionDate": "2024-01-16",
  "paymentMethod": "Credit Card"
}
```

## 🚀 Deployment

### Docker Deployment
```bash
# Build and run
docker-compose up -d

# View logs
docker-compose logs -f finance-tracker

# Stop services
docker-compose down
```

### Production Considerations
1. **Change default passwords** in production
2. **Use strong JWT secret** (256-bit minimum)
3. **Enable HTTPS** in production
4. **Configure proper database backups**
5. **Set up monitoring and logging**
6. **Use environment variables** for sensitive data

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🆘 Support

For support and questions:
- Create an issue in the repository
- Check the API documentation at `/swagger-ui.html`
- Review the application logs

## 🔄 Future Enhancements

- [ ] Export transactions to CSV/PDF
- [ ] Email notifications for budget alerts
- [ ] Mobile app (React Native/Flutter)
- [ ] Advanced analytics and charts
- [ ] Multi-currency support
- [ ] Recurring transaction automation
- [ ] Integration with banking APIs
- [ ] Financial goal tracking
- [ ] Investment portfolio tracking