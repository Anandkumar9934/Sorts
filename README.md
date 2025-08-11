# Driver Management API

A secure Spring Boot API for managing driver information with encryption and file storage capabilities.

## Features

### 🔐 Security Features
- **AES Encryption**: All sensitive data (phone numbers, license numbers, addresses) are encrypted using AES/GCM/NoPadding
- **File Encryption**: Driver photos and documents are encrypted before storage
- **Input Validation**: Comprehensive validation for all input data
- **CORS Configuration**: Proper CORS setup for cross-origin requests

### 📊 Data Management
- **Driver Information**: Complete driver profile management
- **License Management**: Driving license details with expiry tracking
- **Photo Storage**: Secure storage of profile and license photos
- **Status Tracking**: Driver status management (Active, Inactive, Suspended, etc.)
- **Verification System**: Driver verification workflow

### 🚀 API Features
- **RESTful Design**: Standard REST API endpoints
- **Pagination**: Support for paginated results
- **Search Functionality**: Search drivers by name
- **File Upload**: Secure file upload for photos
- **Status Management**: Update driver status and verification

## Technology Stack

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **Spring Security**
- **H2 Database** (for development)
- **Maven**

## Project Structure

```
src/main/java/com/driverapi/
├── DriverManagementApplication.java    # Main application class
├── config/
│   ├── SecurityConfig.java            # Security configuration
│   └── JpaConfig.java                 # JPA configuration
├── controller/
│   └── DriverController.java          # REST API endpoints
├── dto/
│   ├── DriverRequest.java             # Request DTO
│   └── DriverResponse.java            # Response DTO
├── entity/
│   ├── Driver.java                    # Driver entity
│   └── DriverStatus.java              # Status enum
├── repository/
│   └── DriverRepository.java          # Data access layer
└── service/
    ├── DriverService.java             # Business logic
    ├── EncryptionService.java         # Encryption utilities
    └── FileStorageService.java        # File handling
```

## Setup Instructions

### Prerequisites
- Java 17 or higher
- Maven 3.6 or higher

### 1. Clone and Build
```bash
# Build the project
mvn clean install
```

### 2. Configuration
Update the `application.yml` file with your settings:

```yaml
# Generate a secure encryption key
security:
  encryption:
    key: your-32-character-encryption-key-here  # Replace with a secure key
```

To generate a secure encryption key, you can use the `EncryptionService.generateEncryptionKey()` method.

### 3. Run the Application
```bash
mvn spring-boot:run
```

The API will be available at: `http://localhost:8080/api/v1`

## API Endpoints

### Driver Management

#### Create Driver
```http
POST /api/v1/drivers
Content-Type: application/json

{
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
}
```

#### Get Driver by ID
```http
GET /api/v1/drivers/{id}
```

#### Get All Drivers (with pagination)
```http
GET /api/v1/drivers?page=0&size=10&sortBy=firstName&sortDir=asc
```

#### Update Driver
```http
PUT /api/v1/drivers/{id}
Content-Type: application/json

{
  // Same structure as create request
}
```

#### Delete Driver
```http
DELETE /api/v1/drivers/{id}
```

### File Management

#### Upload Profile Photo
```http
POST /api/v1/drivers/{id}/profile-photo
Content-Type: multipart/form-data

file: [image file]
```

#### Upload License Photo
```http
POST /api/v1/drivers/{id}/license-photo
Content-Type: multipart/form-data

file: [image file]
```

#### Get Profile Photo
```http
GET /api/v1/drivers/{id}/profile-photo
```

#### Get License Photo
```http
GET /api/v1/drivers/{id}/license-photo
```

### Search and Filter

#### Search by Name
```http
GET /api/v1/drivers/search?name=John
```

#### Get Drivers by Status
```http
GET /api/v1/drivers/status/ACTIVE
```

#### Get Drivers with Expired Licenses
```http
GET /api/v1/drivers/expired-licenses
```

### Status Management

#### Update Driver Status
```http
PATCH /api/v1/drivers/{id}/status?status=ACTIVE
```

#### Verify Driver
```http
PATCH /api/v1/drivers/{id}/verify
```

## Security Features

### Data Encryption
- **Phone Numbers**: Encrypted using AES/GCM/NoPadding
- **License Numbers**: Encrypted using AES/GCM/NoPadding
- **Addresses**: Encrypted using AES/GCM/NoPadding
- **Files**: Encrypted before storage

### File Security
- Files are validated for type and size
- Allowed formats: JPG, JPEG, PNG, GIF
- Maximum file size: 10MB
- Files are encrypted and stored securely

### Input Validation
- Email format validation
- Phone number format validation
- Date validation (past dates for birth, future dates for license expiry)
- Required field validation
- Unique constraint validation

## Database Schema

The application uses H2 database with the following main table:

### Drivers Table
- `id` (Primary Key)
- `first_name`, `last_name`
- `email` (Unique)
- `phone_number` (Encrypted)
- `date_of_birth`
- `driving_license_number` (Encrypted, Unique)
- `license_expiry_date`
- `license_issuing_authority`
- `address` (Encrypted)
- `city`, `state`, `country`, `postal_code`
- `profile_photo_path`, `license_photo_path`
- `status`, `is_verified`
- `created_at`, `updated_at`

## Development

### Running Tests
```bash
mvn test
```

### Database Console
Access H2 console at: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:file:./driverdb`
- Username: `sa`
- Password: `password`

### Health Check
```http
GET /api/v1/drivers/health
```

## Production Considerations

### Security
1. **Change Default Encryption Key**: Generate a secure 32-character encryption key
2. **Database**: Use a production database (PostgreSQL, MySQL, etc.)
3. **Authentication**: Implement proper authentication (JWT, OAuth, etc.)
4. **HTTPS**: Enable HTTPS in production
5. **File Storage**: Consider using cloud storage (AWS S3, Azure Blob, etc.)

### Performance
1. **Database Indexing**: Add indexes for frequently queried fields
2. **Caching**: Implement caching for frequently accessed data
3. **File Compression**: Compress images before storage
4. **CDN**: Use CDN for serving images

### Monitoring
1. **Logging**: Implement proper logging
2. **Metrics**: Add application metrics
3. **Health Checks**: Implement comprehensive health checks
4. **Error Handling**: Add proper error handling and monitoring

## License

This project is for educational purposes. Please ensure compliance with local data protection regulations when using in production.

## Support

For questions or issues, please create an issue in the repository.
