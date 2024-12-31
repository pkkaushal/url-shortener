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

