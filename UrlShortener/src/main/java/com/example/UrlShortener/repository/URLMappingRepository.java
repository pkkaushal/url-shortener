package com.example.UrlShortener.repository;

import com.example.UrlShortener.entity.UrlMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface URLMappingRepository extends JpaRepository<UrlMapping,Long> {

    Optional<UrlMapping> findByShortCode(String shortCode);

    boolean existsByShortCode(String shortCode);

    Optional<UrlMapping> findByLongUrl(String longUrl);
}
