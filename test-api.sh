#!/bin/bash

echo "🚀 Testing SkillShare Portal API"
echo "=================================="

# Wait for application to start
echo "⏳ Waiting for application to start..."
sleep 10

BASE_URL="http://localhost:8080"

# Test 1: Register a new user
echo ""
echo "📝 Test 1: Registering a new user..."
REGISTER_RESPONSE=$(curl -s -X POST "$BASE_URL/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123"
  }')

echo "Register Response: $REGISTER_RESPONSE"

# Extract token from register response
TOKEN=$(echo $REGISTER_RESPONSE | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

if [ -z "$TOKEN" ]; then
    echo "❌ Failed to get token from registration"
    exit 1
fi

echo "✅ Registration successful, token: ${TOKEN:0:20}..."

# Test 2: Login
echo ""
echo "🔐 Test 2: Logging in..."
LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }')

echo "Login Response: $LOGIN_RESPONSE"

# Test 3: Add a skill
echo ""
echo "➕ Test 3: Adding a skill..."
SKILL_RESPONSE=$(curl -s -X POST "$BASE_URL/skills/add" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "Java Programming",
    "description": "Expert in Java development with Spring Boot framework",
    "category": "Programming",
    "isPublic": true
  }')

echo "Add Skill Response: $SKILL_RESPONSE"

# Test 4: Get all skills
echo ""
echo "📚 Test 4: Getting all public skills..."
ALL_SKILLS_RESPONSE=$(curl -s -X GET "$BASE_URL/skills/all")
echo "All Skills Response: $ALL_SKILLS_RESPONSE"

# Test 5: Get user's skills
echo ""
echo "👤 Test 5: Getting user skills..."
MY_SKILLS_RESPONSE=$(curl -s -X GET "$BASE_URL/skills/my" \
  -H "Authorization: Bearer $TOKEN")
echo "My Skills Response: $MY_SKILLS_RESPONSE"

echo ""
echo "✅ API testing completed!"
echo "🌐 Frontend available at: $BASE_URL"
echo "📖 Swagger UI available at: $BASE_URL/swagger-ui/index.html"