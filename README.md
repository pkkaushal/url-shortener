# URL Shortener

A URL Shortener service built using Spring Boot.

## Features
- Generate short URLs for long URLs.
- Redirect from short URL to the original URL.
- Validate and store URLs.
- Scalable for millions of users.

## Tech Stack
- **Backend:** Spring Boot
- **Database:** MySQL (or any preferred RDBMS)
- **Build Tool:** Maven
- **Testing Frameworks:** JUnit, Mockito


## Setup and Run
1. Clone the repository:
   ```bash
   git clone https://github.com/pkaushal/url-shortener.git

Endpoints
POST /shorten

Request: { "longUrl": "http://example.com" }
Response: { "shortUrl": "http://yourdomain.com/abc123" }
GET /{shortCode}

Redirects to the original URL.



**2. Configure Database**
Create a MySQL database (or any RDBMS you prefer) and update the database configurations in src/main/resources/application.properties.
Example configuration for MySQL:

properties

spring.datasource.url=jdbc:mysql://localhost:3306/url_shortener_db

spring.datasource.username=*****

spring.datasource.password=yourpassword

spring.jpa.hibernate.ddl-auto=update

## 3. Build the project:
If you're using Maven, run the following command to build the project:

mvn clean install
## 4. Run the project:
To start the Spring Boot application, run:


mvn spring-boot:run
Once the application starts, the service will be available on http://localhost:8080.

## Endpoints
1. POST /api/shorten
Request:
json
Copy code
{
  "longUrl": "http://example.com"
}
Response:
json
Copy code
{
  "shortUrl": "http://yourdomain.com/abc123"
}
This endpoint takes a long URL and returns a shortened URL.

2. GET /api/redirect
Request:
text
Copy code
/api/redirect?code=abc123
Response: Redirects to the original long URL.

## 3. Running Tests
To run the tests with Maven, use the following command:


mvn test
This command will run all the test cases in the project.

4. Running Specific Test Class
To run a specific test class, use this command:

mvn -Dtest=UrlControllerTest test



# Performance Test Report

This document provides a summary of the performance test results based on the conducted load test.

---

## **Overview**

The performance test simulates traffic for two endpoints:  

1. **/shorten**: Returns HTTP 200.  
2. **/redirect**: Returns HTTP 302.  

The test metrics help analyze latency, throughput, and system behavior under varying loads.  

---

## **Summary of Results**

### **Key Metrics**

- **Total Requests**:  
  - `/shorten`: 664  
  - `/redirect`: 332  

- **Iterations**: 332  

### **Latency Analysis**

| Metric                | Avg         | Median (p50) | 90th Percentile (p90) | 99th Percentile (p99) | Min       | Max       |
|-----------------------|-------------|--------------|-----------------------|-----------------------|-----------|-----------|
| **Request Duration**  | 91.35ms     | 67.98ms      | 167.25ms             | 182.25ms             | 2.78ms    | 1.01s     |
| **Receiving Time**    | 468.35µs    | 429.2µs      | 996.37µs             | 1.09ms               | 0s        | 1.47ms    |
| **Waiting Time**      | 90.85ms     | 67.57ms      | 167.16ms             | 182.04ms             | 1.9ms     | 1.01s     |

---

## **Interpretation of Results**

### **1. Response Time Percentiles**  

- **p50 (Median):**  
  The median request duration is **67.98ms**, indicating that most requests complete quickly.  

- **p90 (90th Percentile):**  
  The duration for 90% of requests is **167.25ms** or less, showing acceptable performance under moderate load.  

- **p99 (99th Percentile):**  
  99% of requests complete within **182.25ms**, demonstrating minimal outliers and good performance even under heavy traffic.

### **2. Throughput**  
- **Requests per Second (RPS):**  
  - `/shorten`: 3.68 RPS  
  - `/redirect`: 1.83 RPS  

---

---

## **Test Configuration**

- **Virtual Users (VUs):** 5000  
- **Test Duration:** ~2 minutes  
- **Endpoints Tested:**  
  - `/shorten` (returns HTTP 200)  
  - `/redirect` (returns HTTP 302)  

---

## **Future Improvements**

1. **Increase Load:** Gradually increase the number of Virtual Users (VUs) to identify the system's breaking point.  
2. **Error Handling:** Ensure no failed requests (currently 0% failures).  
3. **Detailed Monitoring:** Include deeper analysis of CPU, memory, and database performance during the test.

---

## **Conclusion**

The system demonstrates consistent performance with minimal variability, as reflected in the small difference between **p90** and **p99**. Further testing under increased load is recommended to ensure scalability.




<img width="829" alt="image" src="https://github.com/user-attachments/assets/25df033f-c6a5-43e9-855e-56816f11754c" />

