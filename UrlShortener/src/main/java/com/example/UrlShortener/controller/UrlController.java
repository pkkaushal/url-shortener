package com.example.UrlShortener.controller;

import com.example.UrlShortener.entity.BatchUrlShortenerResponseDTO;
import com.example.UrlShortener.entity.UrlMapping;
import com.example.UrlShortener.entity.UrlShortenRequestDTO;
import com.example.UrlShortener.entity.User;
import com.example.UrlShortener.service.UrlMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api")
public class UrlController {

    @Autowired
    private UrlMappingService urlMappingService;

    @PostMapping(value = "/shorten", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> shortenUrl(@RequestBody UrlShortenRequestDTO urlShortenRequestDTO, @RequestHeader("api_key") String apiKey) {
        System.out.println("Received URL: " + urlShortenRequestDTO.getLongUrl());

        if (urlShortenRequestDTO.getLongUrl() == null || urlShortenRequestDTO.getLongUrl().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Long URL cannot be null or empty");
        }
        String longUrl = urlShortenRequestDTO.getLongUrl();
        LocalDateTime expirationDate = urlShortenRequestDTO.getExpirationDate();
        String customShortCode = urlShortenRequestDTO.getCustomShortCode();

        String shortUrl = urlMappingService.shortenUrl(longUrl, expirationDate, customShortCode, apiKey,urlShortenRequestDTO.getPassword());

        return ResponseEntity.status(HttpStatus.CREATED).body(shortUrl);

    }

    @GetMapping("/redirect/{shortCode}")
    public ResponseEntity<Void> redirectUrl(@PathVariable String code,@RequestParam(required = false)String password) {
      try {
            String longUrl = urlMappingService.getLongUrl(code, password);

          System.out.println("SENDING URL: " + longUrl);
          if (longUrl == null) {
              return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
          }

          return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(longUrl)).build();
        }
      catch(SecurityException e){
          return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
      }
      catch(Exception ex){
          return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        }



    }

    @DeleteMapping("/shorten/{shortCode}")
    public ResponseEntity<String> deleteShortUrl(@PathVariable String shortCode, @RequestHeader("api_key") String apiKey,@RequestParam(required = false)String password) {

        boolean isDeleted_ = urlMappingService.deleteShortUrl(shortCode, apiKey,password);
        if (isDeleted_) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Short url has been deleted");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("short url not present");

        }
    }


    @GetMapping("/top10")
    public ResponseEntity<List<UrlMapping>> getTop10() {
        if (urlMappingService.getTop10UrlByClickCountAndLastAccess().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(urlMappingService.getTop10UrlByClickCountAndLastAccess());

    }


    @PostMapping(value = "/shorten/batch", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<BatchUrlShortenerResponseDTO>> shortenBatchUrl(@RequestBody List<UrlShortenRequestDTO> urlShortenRequestDTOList, @RequestHeader("api_key") String apiKey) {

        //checking tier of user

        if (!urlMappingService.isEnterpriseUser(apiKey)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Collections.singletonList(new BatchUrlShortenerResponseDTO(null, null, "User is not authorized to use this feature")));
        }

        List<BatchUrlShortenerResponseDTO> responses = new ArrayList<>();

        for (UrlShortenRequestDTO urlShortenRequestDTO : urlShortenRequestDTOList) {
            BatchUrlShortenerResponseDTO response = new BatchUrlShortenerResponseDTO();
            response.setLongUrl(urlShortenRequestDTO.getLongUrl());

            try {

                if (urlShortenRequestDTO.getLongUrl() == null || urlShortenRequestDTO.getLongUrl().isEmpty()) {
                    throw new IllegalArgumentException("Long URL cannot be null or empty");

                }

                String shortUrl = urlMappingService.shortenUrl(urlShortenRequestDTO.getLongUrl(), urlShortenRequestDTO.getExpirationDate(), urlShortenRequestDTO.getCustomShortCode(), apiKey, urlShortenRequestDTO.getPassword());
                response.setShortUrl(shortUrl);

            } catch (IllegalArgumentException ex) {
                response.setError("Invalid request: " + ex.getMessage());
            } catch (Exception ex) {
                response.setError("Unexpected error: " + ex.getMessage());

            }
            responses.add(response);

        }

        boolean allFailed = responses.stream().allMatch(response -> response.getError() != null);
        boolean someFailed = responses.stream().anyMatch(response -> response.getError() != null);

        if (allFailed) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responses);
        } else if (someFailed) {
            return ResponseEntity.status(HttpStatus.MULTI_STATUS).body(responses);
        }
        return ResponseEntity.ok(responses);


    }


    @PutMapping(value = "/shorten/{shortcode}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BatchUrlShortenerResponseDTO> editShortCodes(@PathVariable String shortCode, @RequestBody UrlShortenRequestDTO urlShortenRequestDTO, @RequestHeader("api_key") String apikey) {


        if (!urlMappingService.isEnterpriseUser(apikey)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new BatchUrlShortenerResponseDTO(null, null, "User is not authorized to use this feature"));
        }


        try {

            String updatedShortCode = urlMappingService.editShortCode(shortCode, urlShortenRequestDTO.getLongUrl(), urlShortenRequestDTO.getExpirationDate(), urlShortenRequestDTO.getCustomShortCode(), urlShortenRequestDTO.getPassword(), apikey);

            return ResponseEntity.ok(new BatchUrlShortenerResponseDTO(urlShortenRequestDTO.getLongUrl(), updatedShortCode, null));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BatchUrlShortenerResponseDTO(urlShortenRequestDTO.getLongUrl(), null, ex.getMessage()));

        }


    }


}