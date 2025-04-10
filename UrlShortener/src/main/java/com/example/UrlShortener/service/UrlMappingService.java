package com.example.UrlShortener.service;

import com.example.UrlShortener.entity.UrlMapping;
import com.example.UrlShortener.entity.User;
import com.example.UrlShortener.repository.URLMappingRepository;
import com.example.UrlShortener.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class UrlMappingService {

    @Autowired
    private URLMappingRepository urlMappingRepository;

    @Autowired
    private UserRepository userRepository;


    public boolean shortCodeExists(String shortCode) {
        return urlMappingRepository.existsByShortCodeAndDeletedAtIsNull(shortCode);
    }
    public String shortenUrl(String longUrl, LocalDateTime expirationDate, String customShortCode, String apiKey,String password) {
        String shortCode;
        try {

            User user = userRepository.
                    findUserByApiKey(apiKey).
                    orElseThrow(() -> new RuntimeException("User not found"));
            if (customShortCode == null || customShortCode.isEmpty()) {

                shortCode = generateUniqueCode(8);

            } else {

                shortCode = customShortCode;

                if (shortCodeExists(shortCode)) {
                    throw new RuntimeException("Short code already exists");
                }

            }
            // Normalize the long URL
            longUrl=normalizeUrl(longUrl);
            UrlMapping urlMapping = new UrlMapping();
            urlMapping.setLongUrl(longUrl);
            urlMapping.setShortCode(shortCode);
            urlMapping.setTimestamp(LocalDateTime.now());
            urlMapping.setUser(user);
            urlMapping.setExpirationDate(expirationDate);
            urlMapping.setPassword(password);
            urlMappingRepository.save(urlMapping);
        } catch (Exception ex) {
            throw new RuntimeException("Not able to shorten url");
        }

        return shortCode;

    }

    private String generateUniqueCode(int length) {
        String shortCode;
        do {
            shortCode = getRandomString(length);
        } while (shortCodeExists(shortCode));
        return shortCode;
    }


    public List<UrlMapping> getTop10UrlByClickCountAndLastAccess() {
        Pageable top10 = PageRequest.of(0, 10);
        return urlMappingRepository.findTop10ByOrderByClickCountAndLastAccessedAt(top10);
    }


    public String getLongUrl(String shortCode,String password) {
        UrlMapping urlMapping = urlMappingRepository.findByShortCodeAndPasswordAndDeletedAtIsNull(shortCode,password).
                orElseThrow(() -> new RuntimeException("Code not found"));
        if (urlMapping.getExpirationDate() != null && urlMapping.getExpirationDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("URL has expired");
        }

        String longUrl = urlMapping.getLongUrl();

        if (!longUrl.isEmpty() || !longUrl.isBlank() || !(longUrl == null)) {

            urlMappingRepository.incrementClickCount(shortCode, LocalDateTime.now());
        }
        return longUrl;

    }

    private String getRandomString(int length) {

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
            return "https://" + url;
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


    public boolean deleteShortUrl(String shortCode, String apiKey,String password) {


        Optional<User> user = userRepository.findUserByApiKey(apiKey);
        Optional<UrlMapping> urlMapping = urlMappingRepository.findByShortCodeAndPasswordAndDeletedAtIsNull(shortCode,password);
        if (urlMapping.isEmpty() || user.isEmpty()) {
            return false;
        }

        if (urlMapping.get().getExpirationDate() != null && urlMapping.get().getExpirationDate().isBefore(LocalDateTime.now())) {
            return false;

        }

        if (urlMapping.get().getUser().getId() != user.get().getId()) {
            return false;
        } else {
            urlMappingRepository.softDelete(urlMapping.get().getId(), LocalDateTime.now());
            return true;
        }

    }
    public boolean isEnterpriseUser(String apiKey) {
        Optional<User> user = userRepository.findUserByApiKey(apiKey);

        //chek if user is not empty and tier is enterprise
        if(user.isPresent() && user.get().getTier().equals("enterprise")){
            return true;
        }
        else
            return false;

    }

  public Optional<User>   findUserByApiKey(String apiKey) {
        return userRepository.findUserByApiKey(apiKey);
    }

    public String editShortCode(String shortCode, String longUrl, LocalDateTime expirationDate, String customShortCode, String password, String apikey) {

        User user=userRepository.findUserByApiKey(apikey)
                .orElseThrow(()->new RuntimeException("User not found"));

        UrlMapping urlMapping=urlMappingRepository.findByShortCodeAndPasswordAndDeletedAtIsNull(shortCode,password)
                .orElseThrow(()->new RuntimeException("Short code not found"));

        if(urlMapping.getUser().getId()!=user.getId()) {
            throw new RuntimeException("User not authorized to edit this short code");
        }

        if(urlMapping.getPassword()!=null && !urlMapping.getPassword().equals(password)) {
            throw new RuntimeException("Password is incorrect");
        }

        if(customShortCode!=null && !customShortCode.isEmpty()){
            if(shortCodeExists(customShortCode)){
                throw new RuntimeException("Short code already exists");
            }
            urlMapping.setShortCode(customShortCode);


        }
        if(expirationDate!=null) {
            urlMapping.setExpirationDate(expirationDate);
        }
        if(longUrl!=null && !longUrl.isEmpty()) {
            urlMapping.setLongUrl(longUrl);
        }
        urlMappingRepository.save(urlMapping);
        return urlMapping.getShortCode();
    }
}
