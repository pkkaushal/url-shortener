package com.example.UrlShortener.service;

import com.example.UrlShortener.entity.UrlMapping;
import com.example.UrlShortener.repository.URLMappingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URI;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Random;

@Service
public class UrlMappingService {
    @Autowired
    private URLMappingRepository urlMappingRepository;

    public String shortenUrl(String longUrl) {
        String shortCode;
        try {
            longUrl = validateAndNormalizeUrl(longUrl);
            shortCode = getRandomString(8);

            UrlMapping urlMapping = new UrlMapping();
            urlMapping.setLongUrl(longUrl);
            urlMapping.setShortCode(shortCode);
            urlMapping.setTimestamp(LocalDateTime.now());

            urlMappingRepository.save(urlMapping);
        } catch (Exception ex) {
            throw new RuntimeException("Not able to shorten url");
        }
        return shortCode;

    }

    public String getLongUrl(String shortCode){
        return urlMappingRepository.findByShortCode(shortCode).
                map(UrlMapping::getLongUrl)
                .orElseThrow(() -> new RuntimeException("Code not found"));

    }

    private String getRandomString(int length){

        String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder(length);
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString();
    }


    private String normalizeUrl(String url) {
        url = url.trim().replaceAll("\"", ""); // Remove quotes if present
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            return "http://" + url;
        }

        return url;
    }



    private boolean isValidUrl(String url) {
        try {
            new URI(url).toURL();
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    private boolean isUrlAccessible(String url) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("HEAD");
            connection.setConnectTimeout(3000); // Timeout in milliseconds
            connection.setReadTimeout(3000);
            int responseCode = connection.getResponseCode();
            return (responseCode >= 200 && responseCode < 400);
        } catch (Exception e) {
            return false;
        }
    }



    private String enforceHttps(String url) {
        if (url.startsWith("http://")) {
            return url.replace("http://", "https://");
        }
        return url;
    }
    public String validateAndNormalizeUrl(String url) {
        // Add scheme if missing
        url = normalizeUrl(url);

        // Validate basic format
        if (!isValidUrl(url)) {
            throw new IllegalArgumentException("Invalid URL format.");
        }

        // Check reachability
        if (!isUrlAccessible(url)) {
            throw new IllegalArgumentException("URL is not reachable.");
        }



        // Enforce HTTPS
        return enforceHttps(url);
    }



}
