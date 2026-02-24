# Office Seat Allocation System

A production-ready office seat allocation system built with Spring Boot 3, MongoDB, and Thymeleaf. This system manages seat bookings for a 50-seat office with designated and floating seats, supporting batch-based allocation with complex business rules.

## 🏗️ Architecture

### Technology Stack
- **Backend**: Spring Boot 3.3.2, Java 17
- **Database**: MongoDB with Spring Data MongoDB
- **Security**: Spring Security with BCrypt password encoding
- **Frontend**: Thymeleaf with Bootstrap 5
- **Charts**: Chart.js for analytics
- **Mapping**: MapStruct for DTO conversions
- **Validation**: Jakarta Validation
- **Scheduling**: Spring @Scheduled for automated tasks

### Office Configuration
- **Total Seats**: 50
- **Designated Seats**: 40 (Seats 1-40)
- **Floating Seats**: 10 (Seats 41-50)
- **Batches**: 2 batches with alternating weekly schedules

## 📅 Batch System

### Batch 1 Schedule
- **Week 1**: Monday to Wednesday
- **Week 2**: Thursday to Friday

### Batch 2 Schedule
- **Week 1**: Thursday to Friday  
- **Week 2**: Monday to Wednesday

The cycle repeats every 2 weeks automatically.

## 🎯 Business Rules

### Booking Rules
- ❌ No booking in past dates
- ❌ No booking on holidays
- ❌ No double booking of same seat
- ❌ No user booking multiple seats same day
- ❌ Cannot apply leave in past

### Designated Seat Rules
- ✅ Only correct batch can book on designated days
- ✅ Cross-batch booking allowed after 6 PM previous day (if seat unused)

### Floating Seat Rules
- ✅ Anyone can book floating seats
- ⏰ If booking tomorrow on non-designated day: only after 3 PM today

### Leave Integration
- ✅ Applying leave automatically cancels existing booking for that date

## 🚀 Quick Start

### Prerequisites
- Java 17 or higher
- Maven 3.6 or higher
- MongoDB 4.4 or higher

### MongoDB Setup

#### Option 1: Local Installation
```bash
# Install MongoDB
# For Windows: Download and install from https://www.mongodb.com/try/download/community
# For macOS: brew install mongodb-community
# For Linux: sudo apt-get install mongodb

# Start MongoDB
# Windows: Start MongoDB service
# macOS/Linux: brew services start mongodb-community / sudo systemctl start mongod
```

#### Option 2: Docker
```bash
docker run -d -p 27017:27017 --name mongodb mongo:latest
```

### Application Setup

1. **Clone the repository**
```bash
git clone <repository-url>
cd seat-allocation
```

2. **Configure MongoDB**
Edit `src/main/resources/application.yml` if needed:
```yaml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/seat_allocation
```

3. **Build and run the application**
```bash
# Build the project
mvn clean compile

# Run the application
mvn spring-boot:run
```

4. **Access the application**
- **URL**: http://localhost:8080
- **Login Page**: http://localhost:8080/login

## 👤 Default Credentials

### Admin Users
- **Email**: admin@office.com
- **Password**: admin123

- **Email**: sysadmin@office.com  
- **Password**: admin123

### Employee Users
- **Email**: emp1@office.com (Batch 1)
- **Password**: emp123

- **Email**: emp11@office.com (Batch 2)
- **Password**: emp123

*20 employee accounts are created automatically (emp1@office.com to emp20@office.com)*

## 📱 Features

### Dashboard
- 📊 Real-time seat utilization statistics
- 📈 Weekly utilization charts (Chart.js)
- 🎯 Floating seat usage metrics
- 📅 Upcoming holidays display
- 👤 Personal booking overview

### Seat Booking
- 🗓️ Date picker with validation
- 🪑 Interactive 50-seat grid layout
- 🎨 Color-coded seat status:
  - 🟢 Green: Available
  - 🔴 Red: Booked
  - 🟡 Yellow: Floating
  - ⚫ Grey: Holiday
- 💡 Seat tooltips with booking details
- ⏰ Real-time availability updates

### Leave Management
- 📅 Leave application system
- 🔄 Automatic booking cancellation on leave
- ⛔ Past date validation

### Admin Panel
- 👥 User management (CRUD operations)
- 🏖️ Holiday management
- 📊 System analytics
- 📈 Utilization reports
- 🎛️ Administrative controls

### Analytics & Reporting
- 📊 Daily/weekly utilization percentages
- 🎯 Floating seat usage analysis
- 📈 Designated seat wastage tracking
- 📅 Historical booking data

## 🔧 Configuration

### Office Settings
Edit `application.yml` to customize office capacity:
```yaml
seat-allocation:
  office:
    total-seats: 50
    floating-seats: 10
    designated-seats: 40
```

### Logging Configuration
```yaml
logging:
  level:
    root: INFO
    org.springframework.security: INFO
    org.springframework.data.mongodb.core.MongoTemplate: INFO
```

## 🔄 Scheduled Tasks

### Weekly Cycle Reset
- **When**: Every Sunday at midnight
- **What**: Cleans up old cancelled bookings (older than 30 days)
- **Logs**: Current cycle week information

### Daily Cleanup
- **When**: Every day at 1 AM
- **What**: Processes daily maintenance tasks
- **Logs**: Booking statistics and system health

## 🛡️ Security Features

- 🔐 Spring Security integration
- 🔑 BCrypt password encryption
- 👥 Role-based access control (ADMIN/EMPLOYEE)
- 🚫 Protected routes
- 📝 Custom login page
- 🔒 CSRF protection

## 📊 Database Schema

### Collections & Indexes

#### Users
```javascript
// Unique index on email
db.users.createIndex({ "email": 1 }, { unique: true })
```

#### Bookings
```javascript
// Compound indexes
db.bookings.createIndex({ "seatId": 1, "bookingDate": 1 }, { unique: true })
db.bookings.createIndex({ "userId": 1, "bookingDate": 1 }, { unique: true })
```

#### Holidays
```javascript
// Unique index on date
db.holidays.createIndex({ "date": 1 }, { unique: true })
```

#### Leaves
```javascript
// Compound index
db.leaves.createIndex({ "userId": 1, "date": 1 }, { unique: true })
```

## 🧪 Testing

### Run Tests
```bash
mvn test
```

### Test Coverage
- ✅ Service layer business logic
- ✅ Repository operations
- ✅ Security configurations
- ✅ Validation rules

## 🐛 Troubleshooting

### Common Issues

#### MongoDB Connection Error
```
Solution: Ensure MongoDB is running on localhost:27017
Check application.yml MongoDB URI configuration
```

#### Authentication Issues
```
Solution: Verify user credentials in database
Check password encoding (BCrypt)
Ensure user roles are properly set
```

#### Booking Validation Errors
```
Solution: Check business rules implementation
Verify batch schedule calculations
Ensure date validations are working
```

### Debug Mode
Enable debug logging:
```yaml
logging:
  level:
    com.company.seatallocation: DEBUG
    org.springframework.security: DEBUG
```

## 📚 API Endpoints

### Public Endpoints
- `GET /login` - Login page
- `GET /css/*`, `GET /js/*` - Static resources

### User Endpoints
- `GET /dashboard` - User dashboard
- `GET /book-seat` - Seat booking page
- `POST /book-seat` - Process booking
- `GET /api/seats?date=YYYY-MM-DD` - Get seat grid
- `POST /cancel-booking` - Cancel booking
- `GET /leave` - Leave management
- `POST /leave` - Apply leave

### Admin Endpoints
- `GET /admin/panel` - Admin dashboard
- `GET /admin/users` - User management
- `POST /admin/users` - Create user
- `GET /admin/holidays` - Holiday management
- `POST /admin/holidays` - Add holiday

## 🔄 Development Workflow

### Adding New Features
1. Update entities if needed
2. Add repository methods
3. Implement service logic with business rules
4. Create/update controllers
5. Add/update Thymeleaf templates
6. Test thoroughly

### Database Changes
- MongoDB schema changes are handled automatically
- Indexes are created via annotations
- Seed data is managed in `DataInitializer`

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## 📞 Support

For support and questions:
- Create an issue in the repository
- Check the troubleshooting section
- Review the business rules documentation

---

**Built with ❤️ using Spring Boot 3 and MongoDB**
=======
# Wissen-Project
This is my project as instructed by sir on Seat Booking System, i made it using Spring-Boot with Thymleaf for frontend.
>>>>>>> fa939d5b181c20cee64f64f0f5620d20009d70c5
