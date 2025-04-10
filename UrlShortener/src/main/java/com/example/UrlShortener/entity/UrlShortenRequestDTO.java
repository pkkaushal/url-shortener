package com.example.UrlShortener.entity;

import org.antlr.v4.runtime.misc.NotNull;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;

public class UrlShortenRequestDTO {

    public UrlShortenRequestDTO(String longUrl, String password, String customShortCode, LocalDateTime expirationDate) {
        this.longUrl = longUrl;
        this.password = password;
        this.customShortCode = customShortCode;
        this.expirationDate = expirationDate;
    }

    public UrlShortenRequestDTO(){

    }

    @NotNull
    private String longUrl;


    private String password;


   private String customShortCode;
    private LocalDateTime expirationDate;


    public String getCustomShortCode() {
        return customShortCode;
    }

    public void setCustomShortCode(String customShortCode) {
        this.customShortCode = customShortCode;
    }



    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDateTime expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getLongUrl() {
        return longUrl;
    }

    public void setLongUrl(String longUrl) {
        this.longUrl = longUrl;
    }

    public String getPassword() {
        return password;
    }
   public void setPassword(String password) {
        this.password = password;
    }
}
