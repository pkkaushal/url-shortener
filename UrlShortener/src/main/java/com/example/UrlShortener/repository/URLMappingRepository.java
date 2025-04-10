package com.example.UrlShortener.repository;

import com.example.UrlShortener.entity.UrlMapping;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.*;


@Repository
public interface URLMappingRepository extends JpaRepository<UrlMapping,Long> {

    Optional<UrlMapping> findByShortCodeAndPasswordAndDeletedAtIsNull(String shortCode,String password);

    boolean existsByShortCodeAndDeletedAtIsNull(String shortCode);

    Optional<UrlMapping> findByLongUrlAndDeletedAtIsNull(String longUrl);

    @Transactional
    @Modifying
    @Query("update UrlMapping u set u.deletedAt = :deletedAt where u.id = :id")
    void softDelete(@Param("id") Long id, @Param("deletedAt") LocalDateTime deletedAt);


    @Transactional
    @Modifying
    @Query("update UrlMapping u set u.clickCount = u.clickCount + 1, u.lastAccessedAt = :lastAccessedAt where u.shortCode = :shortCode AND u.deletedAt IS NULL")
    void incrementClickCount(@Param("shortCode") String shortCode, @Param("lastAccessedAt") LocalDateTime lastAccessedAt);


    @Query("select u from UrlMapping u WHERE u.deletedAt IS NULL order by u.clickCount DESC, u.lastAccessedAt desc")
    List<UrlMapping> findTop10ByOrderByClickCountAndLastAccessedAt(Pageable page);


    @Query("SELECT u FROM UrlMapping u WHERE u.user.id = :userId AND u.deletedAt IS NULL")
    List<UrlMapping> findByUserId(@Param("userId") Long userId);
}
