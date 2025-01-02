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



<img width="829" alt="image" src="https://github.com/user-attachments/assets/25df033f-c6a5-43e9-855e-56816f11754c" />

