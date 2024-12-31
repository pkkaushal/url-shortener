package com.example.UrlShortener.controller;

import com.example.UrlShortener.service.UrlMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api")
public class UrlController {

   @Autowired
    private UrlMappingService urlMappingService;

  @PostMapping(value = "/shorten", consumes = MediaType.TEXT_PLAIN_VALUE)
   public String shortenUrl(@RequestBody String longUrl){
      System.out.println("Received URL: " + longUrl);




      String shortUrl = urlMappingService.shortenUrl(longUrl);

      return shortUrl;

  }

  @GetMapping("/redirect")
  public ResponseEntity<Void> redirectUrl(@RequestParam String code){

   String longUrl=  urlMappingService.getLongUrl(code);
      System.out.println("SENDING URL: " + longUrl);

   return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(longUrl)).build();
  }
}
