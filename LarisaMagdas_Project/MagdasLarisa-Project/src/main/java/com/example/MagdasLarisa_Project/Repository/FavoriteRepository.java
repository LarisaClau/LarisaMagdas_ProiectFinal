package com.example.MagdasLarisa_Project.Repository;

import com.example.MagdasLarisa_Project.Models.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    Optional<Favorite> findByUserIdAndBookId(Long userId, Long bookId);
    List<Favorite> findByUserId(Long userId);
    void deleteByUserIdAndBookId(Long userId, Long bookId);
}
