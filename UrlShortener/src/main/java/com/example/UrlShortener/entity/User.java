package com.example.UrlShortener.entity;


import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name="users")
public class User
{
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;


    private String name;


    @Column(unique=true,nullable = false)
    private String email;


    @Column(name="api_key" ,unique = true,nullable = false)
    private String apiKey;

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }



    @Column(name="created_at" ,updatable = false)
    private LocalDateTime createdAt=LocalDateTime.now();

    @Column(nullable = false)
    private String tier = "hobby";


    public String getTier() {
        return tier;
    }

    public void setTier(String tier) {
        this.tier = tier;
    }
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

}
