package com.example.MagdasLarisa_Project.Models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

@Entity
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(hidden = true)
    private Long id;

    @Schema(description = "The title of the book", example = "Clean Code", required = true)
    private String title;

    @Schema(description = "The author of the book", example = "Robert C. Martin", required = true)
    private String author;

    @Schema(description = "The genre of the book", example = "Programming", required = true)
    private String genre;

    @Schema(description = "The year the book was published", example = "2008", required = true)
    private int publishedYear;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @Schema(description = "The user who added the book")
    private User user;

    public Book() {}

    public Book(String title, String author, String genre, int publishedYear, User user) {
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.publishedYear = publishedYear;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getPublishedYear() {
        return publishedYear;
    }

    public void setPublishedYear(int publishedYear) {
        this.publishedYear = publishedYear;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
