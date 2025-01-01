package com.example.UrlShortener;


import com.example.UrlShortener.controller.UrlController;
import com.example.UrlShortener.entity.UrlMapping;
import com.example.UrlShortener.repository.URLMappingRepository;
import com.example.UrlShortener.service.UrlMappingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class UrlControllerTest {


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

        when(urlMappingService.shortenUrl(longUrl)).thenReturn(shortCode);

        mockMvc.perform(post("/api/shorten")
                        .contentType(MediaType.TEXT_PLAIN_VALUE)
                        .content(longUrl))
                .andExpect(status().isOk())
                .andExpect(content().string(shortCode));

        verify(urlMappingService, times(1)).shortenUrl(longUrl);
    }

    @Test
    void testRedirectUrl() throws Exception {
        String shortCode = "u8LWS3Am";
        String longUrl = "https://example.com";

        when(urlMappingService.getLongUrl(shortCode)).thenReturn(longUrl);

        mockMvc.perform(get("/api/redirect")
                        .param("code", shortCode))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", longUrl));

        verify(urlMappingService, times(1)).getLongUrl(shortCode);
    }

    @Test
    public void testShortenUrl_WhenLongUrlAlreadyExists() {
        // Mock the repository to return an existing URL mapping
        String longUrl = "https://example.com";
        String existingShortCode = "u8LWS3Am";
        UrlMapping mockMapping = new UrlMapping();
        mockMapping.setLongUrl(longUrl);
        mockMapping.setShortCode(existingShortCode);

        when(urlMappingRepository.findByLongUrl(longUrl)).thenReturn(Optional.of(mockMapping));
        when(urlMappingService.shortenUrl(longUrl)).thenReturn(existingShortCode);
        // Call the service method
        String shortCode = urlMappingService.shortenUrl(longUrl);

        // Assert the existing short code is returned
        assertEquals(existingShortCode, shortCode);
        verify(urlMappingRepository, never()).save(any());
    }

}
