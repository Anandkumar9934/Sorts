#!/bin/bash

# Driver Management API Test Script
# Make sure the application is running on http://localhost:8080

BASE_URL="http://localhost:8080/api/v1/drivers"

echo "🚗 Testing Driver Management API"
echo "=================================="

# Health check
echo "1. Health Check"
curl -X GET "$BASE_URL/health" -H "Content-Type: application/json"
echo -e "\n\n"

# Create a driver
echo "2. Creating a new driver"
DRIVER_RESPONSE=$(curl -s -X POST "$BASE_URL" \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "phoneNumber": "+1234567890",
    "dateOfBirth": "1990-01-01",
    "drivingLicenseNumber": "DL123456789",
    "licenseExpiryDate": "2025-12-31",
    "licenseIssuingAuthority": "DMV",
    "address": "123 Main St",
    "city": "New York",
    "state": "NY",
    "country": "USA",
    "postalCode": "10001"
  }')

echo "$DRIVER_RESPONSE"
echo -e "\n\n"

# Extract driver ID from response (assuming it's the first created driver with ID 1)
DRIVER_ID=1

# Get driver by ID
echo "3. Getting driver by ID: $DRIVER_ID"
curl -X GET "$BASE_URL/$DRIVER_ID" -H "Content-Type: application/json"
echo -e "\n\n"

# Get all drivers
echo "4. Getting all drivers (first page)"
curl -X GET "$BASE_URL?page=0&size=5" -H "Content-Type: application/json"
echo -e "\n\n"

# Search drivers by name
echo "5. Searching drivers by name: John"
curl -X GET "$BASE_URL/search?name=John" -H "Content-Type: application/json"
echo -e "\n\n"

# Get drivers by status
echo "6. Getting drivers by status: ACTIVE"
curl -X GET "$BASE_URL/status/ACTIVE" -H "Content-Type: application/json"
echo -e "\n\n"

# Update driver status
echo "7. Updating driver status to VERIFIED"
curl -X PATCH "$BASE_URL/$DRIVER_ID/verify" -H "Content-Type: application/json"
echo -e "\n\n"

# Get drivers with expired licenses
echo "8. Getting drivers with expired licenses"
curl -X GET "$BASE_URL/expired-licenses" -H "Content-Type: application/json"
echo -e "\n\n"

echo "✅ API testing completed!"
echo "Note: File upload tests require actual image files and are not included in this script."
echo "To test file uploads, use tools like Postman or curl with -F flag for multipart/form-data."