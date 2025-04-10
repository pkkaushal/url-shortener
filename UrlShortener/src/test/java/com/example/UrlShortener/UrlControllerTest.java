package com.example.UrlShortener;

import com.example.UrlShortener.controller.UrlController;
import com.example.UrlShortener.entity.UrlMapping;
import com.example.UrlShortener.entity.UrlShortenRequestDTO;
import com.example.UrlShortener.entity.User;
import com.example.UrlShortener.repository.URLMappingRepository;
import com.example.UrlShortener.service.UrlMappingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class UrlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @InjectMocks
    private UrlController urlController;

    @Mock
    private URLMappingRepository urlMappingRepository;

    @Mock
    private UrlMappingService urlMappingService;

    @BeforeEach
    void setup() {
        // MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(urlController).build();
    }

    @Test
    void testShortenUrl() throws Exception {
        String longUrl = "https://example.com";
        String shortCode = "u8LWS3Am";
        String apiKey = "testApiKey";
        UrlShortenRequestDTO requestDTO = new UrlShortenRequestDTO();
        requestDTO.setLongUrl(longUrl);
        when(urlMappingService.shortenUrl(longUrl, null, null, apiKey)).thenReturn(shortCode);

        mockMvc.perform(post("/api/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("api_key", apiKey)
                        .content("{\"longUrl\":\"" + longUrl + "\"}"))
                .andExpect(status().isCreated())
                .andExpect(content().string(shortCode));

        verify(urlMappingService, times(1)).shortenUrl(longUrl, null, null, apiKey);
    }

    @Test
    void testRedirectUrl() throws Exception {
        String longUrl = "https://example.com";
        String shortCode = "u8LWS3Am";

        // when(urlMappingService.shortenUrl(longUrl)).thenReturn(shortCode);
        when(urlMappingService.getLongUrl(shortCode,password)).thenReturn(longUrl);

        mockMvc.perform(get("/api/redirect")
                        .param("code", shortCode))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", longUrl));

        verify(urlMappingService, times(1)).getLongUrl(shortCode);
    }

    @Test
    void testShortenUrl_WhenLongUrlAlreadyExists() throws Exception {
        String longUrl = "https://example.com";
        String existingShortCode = "u8LWS3Am";
        String apiKey = "testApiKey";
        UrlShortenRequestDTO requestDTO = new UrlShortenRequestDTO();
        requestDTO.setLongUrl(longUrl);
        // Save the existing URL mapping in the repository
//        UrlMapping existingMapping = new UrlMapping();
//        existingMapping.setLongUrl(longUrl);
//        existingMapping.setShortCode(existingShortCode);
//        existingMapping.setUser(new User());
//        existingMapping.setTimestamp(LocalDateTime.now());
        when(urlMappingService.shortenUrl(longUrl, null, null, apiKey)).thenReturn(existingShortCode);

        mockMvc.perform(post("/api/shorten")
                        .contentType(MediaType.APPLICATION_JSON_VALUE).header("api_key", apiKey)
                        .content(new ObjectMapper().writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().string(existingShortCode));

        // Verify that no new URL mapping is saved
        verify(urlMappingService, times(1)).shortenUrl(longUrl, null, null, apiKey);
        //verify(urlMappingRepository, never()).save(any());
    }


    @Test
    void testShortenUrl_WhenLongUrlIsNull() throws Exception {
        String apiKey = "testApiKey";
        UrlShortenRequestDTO requestDTO = new UrlShortenRequestDTO();
        requestDTO.setLongUrl("");


        mockMvc.perform(post("/api/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("api_key", apiKey)
                        .content(new ObjectMapper().writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Long URL cannot be null or empty"));


    }

    @Test
    void testShortenUrl_WhenLongUrlIsEmpty() throws Exception {
        String apiKey = "testApiKey";
        UrlShortenRequestDTO requestDTO = new UrlShortenRequestDTO();
        requestDTO.setLongUrl("");
        mockMvc.perform(post("/api/shorten")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("api_key", apiKey)
                        .content(new ObjectMapper().writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Long URL cannot be null or empty"));
    }

    @Test
    void testDeleteShortUrl() throws Exception {
        String shortCode = "u8LWS3Am";
        String apiKey = "testApiKey";
        when(urlMappingService.deleteShortUrl(shortCode, apiKey)).thenReturn(true);

        mockMvc.perform(delete("/api/shorten/{shortCode}", shortCode)
                        .header("api_key", apiKey))
                .andExpect(status().isNoContent())
                .andExpect(content().string("Short url has been deleted"));

        verify(urlMappingService, times(1)).deleteShortUrl(shortCode, apiKey);
    }

    @Test
    void testDeleteShortUrl_NotFound() throws Exception {
        String shortCode = "nonExistentCode";
        String apiKey = "testApiKey";
        when(urlMappingService.deleteShortUrl(shortCode, apiKey)).thenReturn(false);

        mockMvc.perform(delete("/api/shorten/{shortCode}", shortCode)
                        .header("api_key", apiKey))
                .andExpect(status().isNotFound())
                .andExpect(content().string("short url not present"));

        verify(urlMappingService, times(1)).deleteShortUrl(shortCode, apiKey);
    }

    void testShortenUrl_WithExpirationDate() throws Exception {
        String longUrl = "https://example.com";
        String shortCode = "u8LWS3Am";
        String apiKey = "testApiKey";
        LocalDateTime expirationDate = LocalDateTime.now().plusDays(1);
        UrlShortenRequestDTO requestDTO = new UrlShortenRequestDTO();
        requestDTO.setLongUrl(longUrl);
        requestDTO.setExpirationDate(expirationDate);
        when(urlMappingService.shortenUrl(longUrl, expirationDate, null, apiKey)).thenReturn(shortCode);

        mockMvc.perform(post("/api/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("api_key", apiKey)
                        .content(new ObjectMapper().writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().string(shortCode));
    }

    @Test
    void testShortenUrl_WithCustomShortCode() throws Exception {
        String longUrl = "https://example.com";
        String shortCode = "customCode";
        UrlShortenRequestDTO requestDTO = new UrlShortenRequestDTO();
        requestDTO.setLongUrl(longUrl);
        requestDTO.setCustomShortCode(shortCode);
        when(urlMappingService.shortCodeExists(shortCode)).thenReturn(false);
        when(urlMappingService.shortenUrl(longUrl, null, shortCode, null)).thenReturn(shortCode);

        mockMvc.perform(post("/api/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().string(shortCode));

    }

    @Test
    void testShortenBatchUrl_Success() throws Exception{

        List<UrlShortenRequestDTO> requests= List.of(
                new UrlShortenRequestDTO("https://example.com/1", null,null),
                new UrlShortenRequestDTO("https://example.com/2","customCode",null)
        );

        when(urlMappingService.shortenUrl(anyString(), any(), any(), anyString()))
                .thenReturn("https://short.ly/example1", "https://short.ly/customCode");

        when(urlMappingService.isEnterpriseUser(anyString())).thenReturn(true);
        mockMvc.perform(post("/api/shorten/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("api_key", "testApiKey")
                        .content(new ObjectMapper().writeValueAsString(requests)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].shortUrl").value("https://short.ly/example1"))
                .andExpect(jsonPath("$[1].shortUrl").value("https://short.ly/customCode"));

        verify(urlMappingService, times(2)).shortenUrl(anyString(), any(), any(), anyString());

    }

    @Test
    void testShortenBatchUrl_PartialFailure() throws Exception {
        List<UrlShortenRequestDTO> requests = List.of(
                new UrlShortenRequestDTO("https://example.com/1", null, null),
                new UrlShortenRequestDTO(null, "customCode", null)
        );

        when(urlMappingService.shortenUrl(anyString(), any(), any(), anyString()))
                .thenReturn("https://short.ly/example1");
        when(urlMappingService.isEnterpriseUser(anyString())).thenReturn(true);
        mockMvc.perform(post("/api/shorten/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("api_key", "testApiKey")
                        .content(new ObjectMapper().writeValueAsString(requests)))
                .andExpect(status().isMultiStatus())
                .andExpect(jsonPath("$[0].shortUrl").value("https://short.ly/example1"))
                .andExpect(jsonPath("$[1].error").value("Invalid request: Long URL cannot be null or empty"));

        verify(urlMappingService, times(1)).shortenUrl(anyString(), any(), any(), anyString());
    }

    @Test
    void testShortenBatchUrl_Failure() throws Exception {
        List<UrlShortenRequestDTO> requests = List.of(
                new UrlShortenRequestDTO(null, null, null),
                new UrlShortenRequestDTO("", null, null)
        );
        when(urlMappingService.isEnterpriseUser(anyString())).thenReturn(true);
        mockMvc.perform(post("/api/shorten/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("api_key", "testApiKey")
                        .content(new ObjectMapper().writeValueAsString(requests)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].error").value("Invalid request: Long URL cannot be null or empty"))
                .andExpect(jsonPath("$[1].error").value("Invalid request: Long URL cannot be null or empty"));

        verify(urlMappingService, never()).shortenUrl(anyString(), any(), any(), anyString());
    }


    @Test
    void shortenBatchUrl_EnterpriseUser() throws Exception {
        List<UrlShortenRequestDTO> requests = List.of(
                new UrlShortenRequestDTO("https://example.com/1", null, null),
                new UrlShortenRequestDTO("https://example.com/2", "customCode", null)
        );

        when(urlMappingService.isEnterpriseUser(anyString())).thenReturn(true);
        when(urlMappingService.shortenUrl(anyString(), any(), any(), anyString()))
                .thenReturn("https://short.ly/example1", "https://short.ly/customCode");

        mockMvc.perform(post("/api/shorten/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("api_key", "enterpriseApiKey")
                        .content(new ObjectMapper().writeValueAsString(requests)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].shortUrl").value("https://short.ly/example1"))
                .andExpect(jsonPath("$[1].shortUrl").value("https://short.ly/customCode"));

        verify(urlMappingService, times(2)).shortenUrl(anyString(), any(), any(), anyString());
    }

    @Test
    void shortenBatchUrl_HobbyUser() throws Exception {
        List<UrlShortenRequestDTO> requests = List.of(
                new UrlShortenRequestDTO("https://example.com/1", null, null),
                new UrlShortenRequestDTO("https://example.com/2", "customCode", null)
        );

        when(urlMappingService.isEnterpriseUser(anyString())).thenReturn(false);

        mockMvc.perform(post("/api/shorten/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("api_key", "hobbyApiKey")
                        .content(new ObjectMapper().writeValueAsString(requests)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$[0].error").value("User is not authorized to use this feature"));

        verify(urlMappingService, never()).shortenUrl(anyString(), any(), any(), anyString());
    }
}