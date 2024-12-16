package com.example.MagdasLarisa_Project.Repository;

import com.example.MagdasLarisa_Project.Models.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
}