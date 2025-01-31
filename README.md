# URL Shortener

A URL Shortener service built using Spring Boot.

## Features

- Generate short URLs for long URLs.
- Redirect from short URL to the original URL.
- Validate and store URLs.
- Scalable for millions of users.
- **Bulk URL creation** for enterprise users.
- **Enterprise and Hobby user support** with different privileges.
- **Password protection** for short URLs.
- **Short code activation/deactivation** via expiry timestamp updates.
- **View all URLs for an enterprise user.**

## Tech Stack

- **Backend:** Spring Boot
- **Database:** MySQL (or any preferred RDBMS)
- **Build Tool:** Maven
- **Testing Frameworks:** JUnit, Mockito

## Setup and Run

### 1. Clone the repository:

```bash
git clone https://github.com/pkaushal/url-shortener.git
```

### 2. Configure Database

Create a MySQL database (or any RDBMS you prefer) and update the database configurations in `src/main/resources/application.properties`.

Example configuration for MySQL:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/url_shortener_db
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
```

### 3. Build the project:

```bash
mvn clean install
```

### 4. Run the project:

To start the Spring Boot application, run:

```bash
mvn spring-boot:run
```

Once the application starts, the service will be available on `http://localhost:8080`.

## Endpoints

### 1. Shorten a URL

**POST** `/api/shorten`

#### Request:

```json
{
  "longUrl": "http://example.com"
}
```

#### Response:

```json
{
  "shortUrl": "http://yourdomain.com/abc123"
}
```

### 2. Redirect to Original URL

**GET** `/api/redirect`

#### Request:

```
/api/redirect?code=abc123
```

#### Response:

Redirects to the original long URL.

### 3. Bulk URL Creation (Enterprise Users)

**POST** `/api/batch`

#### Request:

```json
{
  "requests": [
    {
      "longUrl": "http://example1.com",
      "customShortCode": "custom1",
      "expirationDate": "2024-12-31T23:59:59"
    },
    {
      "longUrl": "http://example2.com",
      "expirationDate": "2025-06-30T23:59:59"
    }
  ]
}
```

#### Response:

```json
{
  "responses": [
    {
      "longUrl": "http://example1.com",
      "shortUrl": "http://yourdomain.com/custom1",
      "error": null
    },
    {
      "longUrl": "http://example2.com",
      "shortUrl": "http://yourdomain.com/a1b2c3",
      "error": null
    }
  ]
}
```

### 4. Password Protection for Short URLs

**POST** `/api/shorten`

#### Request:

```json
{
  "longUrl": "http://example.com",
  "password": "mypassword"
}
```

**GET** `/api/redirect`

#### Request:

```
/api/redirect?code=abc123&password=mypassword
```

### 5. Activate/Deactivate Short Code

**PUT** `/api/shortcode/update-expiry`

#### Request:

```json
{
  "shortCode": "abc123",
  "expiryDate": "2024-01-01T00:00:00Z"
}
```

### 6. View All URLs for an Enterprise User

**GET** `/api/user/urls`

#### Response:

```json
[
  {
    "shortCode": "abc123",
    "longUrl": "http://example.com",
    "createdAt": "2023-12-01T12:00:00Z",
    "expiryDate": "2024-01-01T00:00:00Z"
  },
  {
    "shortCode": "xyz789",
    "longUrl": "http://example2.com",
    "createdAt": "2023-12-05T15:30:00Z",
    "expiryDate": "2024-02-01T00:00:00Z"
  }
]
```

## Running Tests

To run all tests:

```bash
mvn test
```

To run a specific test class:

```bash
mvn -Dtest=UrlControllerTest test
```

---

# Performance Test Report

## **Overview**

The performance test simulates traffic for multiple endpoints, including bulk creation and password-protected redirects.

## **Summary of Results**

### **Key Metrics**

- **Total Requests**:

  - `/shorten`: 664
  - `/redirect`: 332
  - `/bulk-shorten`: 220

- **Iterations**: 332

### **Latency Analysis**

| Metric               | Avg      | Median (p50) | 90th Percentile (p90) | 99th Percentile (p99) | Min    | Max    |
| -------------------- | -------- | ------------ | --------------------- | --------------------- | ------ | ------ |
| **Request Duration** | 91.35ms  | 67.98ms      | 167.25ms              | 182.25ms              | 2.78ms | 1.01s  |
| **Receiving Time**   | 468.35µs | 429.2µs      | 996.37µs              | 1.09ms                | 0s     | 1.47ms |
| **Waiting Time**     | 90.85ms  | 67.57ms      | 167.16ms              | 182.04ms              | 1.9ms  | 1.01s  |

### **Throughput**

- **Requests per Second (RPS):**
  - `/shorten`: 3.68 RPS
  - `/redirect`: 1.83 RPS
  - `/bulk-shorten`: 2.50 RPS

## **Future Improvements**

1. **Optimize Bulk Processing:** Improve efficiency for enterprise users creating thousands of URLs at once.
2. **Enhance Security:** Strengthen password protection for short URLs.
3. **Scalability Testing:** Increase the number of concurrent users to identify system limits.

## **Conclusion**

The system demonstrates stable performance with efficient handling of bulk URL creation and password-protected short codes. Further optimizations can improve scalability and security.

