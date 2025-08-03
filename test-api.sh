#!/bin/bash

# Test script for Personal Finance Tracker API
# Make sure the application is running on localhost:8080

BASE_URL="http://localhost:8080/api"
echo "Testing Personal Finance Tracker API..."
echo "======================================"

# Test 1: Register a new user
echo "1. Testing user registration..."
REGISTER_RESPONSE=$(curl -s -X POST "$BASE_URL/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123",
    "firstName": "Test",
    "lastName": "User",
    "monthlyBudget": 3000.00,
    "currency": "USD"
  }')

echo "Register Response: $REGISTER_RESPONSE"

# Extract token from registration response
TOKEN=$(echo $REGISTER_RESPONSE | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

if [ -z "$TOKEN" ]; then
    echo "Failed to get token from registration. Trying login..."
    LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/auth/login" \
      -H "Content-Type: application/json" \
      -d '{
        "username": "testuser",
        "password": "password123"
      }')
    
    TOKEN=$(echo $LOGIN_RESPONSE | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
fi

if [ -z "$TOKEN" ]; then
    echo "Failed to authenticate. Exiting."
    exit 1
fi

echo "Token obtained: ${TOKEN:0:20}..."

# Test 2: Create an income category
echo "2. Creating income category..."
INCOME_CATEGORY_RESPONSE=$(curl -s -X POST "$BASE_URL/categories" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "Salary",
    "type": "INCOME",
    "description": "Monthly salary",
    "colorHex": "#27ae60"
  }')

echo "Income Category Response: $INCOME_CATEGORY_RESPONSE"

# Test 3: Create an expense category
echo "3. Creating expense category..."
EXPENSE_CATEGORY_RESPONSE=$(curl -s -X POST "$BASE_URL/categories" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "Food",
    "type": "EXPENSE",
    "description": "Food and dining",
    "colorHex": "#e74c3c"
  }')

echo "Expense Category Response: $EXPENSE_CATEGORY_RESPONSE"

# Test 4: Get all categories
echo "4. Getting all categories..."
CATEGORIES_RESPONSE=$(curl -s -X GET "$BASE_URL/categories" \
  -H "Authorization: Bearer $TOKEN")

echo "Categories Response: $CATEGORIES_RESPONSE"

# Test 5: Add an income transaction
echo "5. Adding income transaction..."
INCOME_TRANSACTION_RESPONSE=$(curl -s -X POST "$BASE_URL/transactions" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "description": "Monthly Salary",
    "amount": 5000.00,
    "type": "INCOME",
    "categoryId": 1,
    "transactionDate": "2024-01-15",
    "paymentMethod": "Direct Deposit"
  }')

echo "Income Transaction Response: $INCOME_TRANSACTION_RESPONSE"

# Test 6: Add an expense transaction
echo "6. Adding expense transaction..."
EXPENSE_TRANSACTION_RESPONSE=$(curl -s -X POST "$BASE_URL/transactions" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "description": "Grocery Shopping",
    "amount": 150.50,
    "type": "EXPENSE",
    "categoryId": 2,
    "transactionDate": "2024-01-16",
    "paymentMethod": "Credit Card"
  }')

echo "Expense Transaction Response: $EXPENSE_TRANSACTION_RESPONSE"

# Test 7: Get all transactions
echo "7. Getting all transactions..."
TRANSACTIONS_RESPONSE=$(curl -s -X GET "$BASE_URL/transactions" \
  -H "Authorization: Bearer $TOKEN")

echo "Transactions Response: $TRANSACTIONS_RESPONSE"

# Test 8: Get financial summary
echo "8. Getting financial summary..."
SUMMARY_RESPONSE=$(curl -s -X GET "$BASE_URL/transactions/summary?startDate=2024-01-01&endDate=2024-12-31" \
  -H "Authorization: Bearer $TOKEN")

echo "Financial Summary Response: $SUMMARY_RESPONSE"

echo "======================================"
echo "API testing completed!"
echo "Check the responses above for any errors."
echo "You can also access the web interface at: http://localhost:8080"
echo "API documentation at: http://localhost:8080/swagger-ui.html"