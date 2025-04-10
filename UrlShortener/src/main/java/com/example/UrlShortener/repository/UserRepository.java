package com.example.UrlShortener.repository;

import com.example.UrlShortener.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

  Optional<User> findUserByApiKey(String apiKey);
}
