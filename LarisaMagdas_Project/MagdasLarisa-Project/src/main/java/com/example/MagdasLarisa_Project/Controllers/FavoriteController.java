package com.example.MagdasLarisa_Project.Controllers;

import com.example.MagdasLarisa_Project.Models.Book;
import com.example.MagdasLarisa_Project.Models.ErrorResponse;
import com.example.MagdasLarisa_Project.Models.Favorite;
import com.example.MagdasLarisa_Project.Models.User;
import com.example.MagdasLarisa_Project.Repository.BookRepository;
import com.example.MagdasLarisa_Project.Repository.FavoriteRepository;
import com.example.MagdasLarisa_Project.Repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Tag(name = "Favorite Books Controller", description = "Allows users to manage their favorite books.")
@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    // Endpoint for adding a book to favorites
    @Operation(summary = "Add a book to favorites", description = "Allows any user to add a book to their favorites list.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "The book was successfully added to favorites."),
            @ApiResponse(responseCode = "404", description = "Book or user not found.")
    })
    @PostMapping("/add")
    public ResponseEntity<?> addToFavorites(
            @Parameter(description = "The username of the user", example = "larisamagdas", required = true) @RequestParam String username,
            @Parameter(description = "The ID of the book", example = "1", required = true) @RequestParam Long bookId) {

        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            return new ResponseEntity<>(new ErrorResponse("User not found", "No user found with the provided username."), HttpStatus.NOT_FOUND);
        }

        Optional<Book> book = bookRepository.findById(bookId);
        if (book.isEmpty()) {
            return new ResponseEntity<>(new ErrorResponse("Book not found", "No book found with the provided ID."), HttpStatus.NOT_FOUND);
        }

        Optional<Favorite> existingFavorite = favoriteRepository.findByUserIdAndBookId(user.get().getId(), bookId);
        if (existingFavorite.isPresent()) {
            return new ResponseEntity<>(new ErrorResponse("Already in favorites", "This book is already in your favorites."), HttpStatus.BAD_REQUEST);
        }

        Favorite favorite = new Favorite(user.get(), book.get());
        favoriteRepository.save(favorite);

        return new ResponseEntity<>(favorite, HttpStatus.CREATED);
    }

    // Endpoint for removing a book from favorites
    @Operation(summary = "Remove a book from favorites", description = "Allows any user to remove a book from their favorites list.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The book was successfully removed from favorites."),
            @ApiResponse(responseCode = "404", description = "Book or user not found.")
    })
    @DeleteMapping("/remove")
    @Transactional
    public ResponseEntity<?> removeFromFavorites(
            @Parameter(description = "The username of the user", example = "larisamagdas", required = true) @RequestParam String username,
            @Parameter(description = "The ID of the book", example = "1", required = true) @RequestParam Long bookId) {

        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            return new ResponseEntity<>(new ErrorResponse("User not found", "No user found with the provided username."), HttpStatus.NOT_FOUND);
        }

        Optional<Book> book = bookRepository.findById(bookId);
        if (book.isEmpty()) {
            return new ResponseEntity<>(new ErrorResponse("Book not found", "No book found with the provided ID."), HttpStatus.NOT_FOUND);
        }

        favoriteRepository.deleteByUserIdAndBookId(user.get().getId(), bookId);

        return new ResponseEntity<>("Book removed from favorites.", HttpStatus.OK);
    }

    // Endpoint for viewing all favorite books of a user
    @Operation(summary = "View all favorite books", description = "Allows any user to view all the books they have added to their favorites.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The list of favorite books was successfully returned."),
            @ApiResponse(responseCode = "404", description = "No favorite books found.")
    })
    @GetMapping("/user/{username}")
    public ResponseEntity<?> getFavorites(
            @Parameter(description = "The username of the user", example = "user1", required = true) @PathVariable String username) {

        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            return new ResponseEntity<>(new ErrorResponse("User not found", "No user found with the provided username."), HttpStatus.NOT_FOUND);
        }

        List<Favorite> favorites = favoriteRepository.findByUserId(user.get().getId());
        if (favorites.isEmpty()) {
            return new ResponseEntity<>(new ErrorResponse("No favorites", "This user has no favorite books."), HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(favorites, HttpStatus.OK);
    }
}
