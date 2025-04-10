package com.example.UrlShortener.entity;

public class BatchUrlShortenerResponseDTO {


    public BatchUrlShortenerResponseDTO() {
    }

    public BatchUrlShortenerResponseDTO(String longUrl, String shortUrl, String error) {
        this.longUrl = longUrl;
        this.shortUrl = shortUrl;
        this.error = error;
    }

    private String longUrl;

    public String getLongUrl() {
        return longUrl;
    }

    public void setLongUrl(String longUrl) {
        this.longUrl = longUrl;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    private String shortUrl;
    private String error;
}
