# E-Commerce Application with Spring Boot and JavaFX

A modern, feature-rich e-commerce application built with Spring Boot backend and JavaFX frontend. This application provides a complete shopping experience with user authentication, product management, shopping cart functionality, and order processing.

## Features

### User Management
- **User Authentication**
  - Secure login system with session management
  - Account locking after 5 failed attempts (30-minute lockout)
  - Session timeout after 30 minutes of inactivity
  - Email and password validation
  - User registration with role-based access

- **Role-Based Access Control**
  - Customer role: Browse products, manage cart, place orders
  - Admin role: Manage products, users, and orders

### Product Management
- **Admin Features**
  - Add new products
  - Edit existing products
  - Delete products
  - Manage product inventory
  - Set product categories and prices

- **Product Display**
  - Grid view of products with images
  - Product details view
  - Price display in Indian Rupees (₹)
  - Stock availability status
  - Category filtering
  - Product search functionality

### Shopping Cart
- **Cart Features**
  - Add/remove products
  - Update quantities
  - Real-time price calculations
  - Persistent cart across sessions
  - Clear cart option
  - Stock validation

### Order Management
- **Customer Features**
  - Place orders
  - View order history
  - Track order status
  - Order details with items and totals

- **Admin Features**
  - View all orders
  - Update order status
  - Filter orders by status
  - View customer details
  - Process orders

### User Interface
- **Modern Design**
  - Clean and intuitive interface
  - Responsive layouts
  - Form validation feedback
  - Success/error notifications
  - Loading indicators
  - Consistent styling

## Technical Stack

### Backend
- **Framework**: Spring Boot
- **Database**: SQL Database (JPA/Hibernate)
- **Security**: Custom session management
- **API**: RESTful services

### Frontend
- **Framework**: JavaFX
- **UI Components**: Custom JavaFX controls
- **Styling**: CSS
- **Architecture**: MVC pattern

## Installation

### Prerequisites
1. Java JDK 17 or higher
2. Maven
3. MySQL Database
4. JavaFX SDK

### Setup Steps
1. Clone the repository:
   ```bash
   git clone [repository-url]
   ```

2. Configure database in `application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/your_database
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

3. Build the project:
   ```bash
   mvn clean install
   ```

4. Run the application:
   ```bash
   mvn spring-boot:run
   ```

## Usage Guide

### Customer Functions
1. **Registration**
   - Click "Register New Account"
   - Fill in required details
   - Automatic assignment of CUSTOMER role

2. **Shopping**
   - Browse products on main page
   - Use filters and search
   - Click product for details
   - Add items to cart

3. **Cart Management**
   - View cart contents
   - Update quantities
   - Remove items
   - Proceed to checkout

4. **Order Placement**
   - Fill shipping details
   - Choose payment method
   - Confirm order
   - View order confirmation

### Admin Functions
1. **Product Management**
   - Add new products
   - Update existing products
   - Manage inventory
   - Set categories

2. **Order Management**
   - View all orders
   - Update order status
   - Process orders
   - View order details

3. **User Management**
   - View user accounts
   - Manage user status
   - Handle user roles

## Security Features

1. **Session Management**
   - 30-minute session timeout
   - Secure session tokens
   - Automatic logout on timeout

2. **Login Security**
   - Account lockout after 5 failed attempts
   - 30-minute lockout duration
   - Email validation
   - Password requirements

3. **Data Validation**
   - Input sanitization
   - Form validation
   - Error handling
   - SQL injection prevention

## Database Schema

### Users Table
- id (Primary Key)
- email (Unique)
- password
- firstName
- lastName
- role
- enabled
- created_at
- updated_at

### Products Table
- id (Primary Key)
- name
- description
- price
- stockQuantity
- category
- imageUrl
- created_at
- updated_at

### Orders Table
- id (Primary Key)
- user_id (Foreign Key)
- totalAmount
- status
- shippingAddress
- paymentMethod
- orderDate
- created_at
- updated_at

### OrderItems Table
- id (Primary Key)
- order_id (Foreign Key)
- product_id (Foreign Key)
- quantity
- price
- subtotal

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## Troubleshooting

### Common Issues
1. **Database Connection**
   - Verify database credentials
   - Check database service status
   - Confirm port availability

2. **JavaFX Issues**
   - Verify JavaFX SDK installation
   - Check module path configuration
   - Confirm VM arguments

3. **Build Problems**
   - Clear Maven cache
   - Update dependencies
   - Check Java version

## License

This project is licensed under the MIT License - see the LICENSE.md file for details.

## Acknowledgments

- Spring Boot framework
- JavaFX community
- Maven repository
- Open-source contributors

## Contact

For support or queries, please contact:
- Email: [your-email]
- GitHub: [your-github-profile]