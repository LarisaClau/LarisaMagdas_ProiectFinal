package com.example.MagdasLarisa_Project.Controllers;

import com.example.MagdasLarisa_Project.Models.Book;
import com.example.MagdasLarisa_Project.Models.ErrorResponse;
import com.example.MagdasLarisa_Project.Models.User;
import com.example.MagdasLarisa_Project.Repository.BookRepository;
import com.example.MagdasLarisa_Project.Repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Tag(name = "Books Controller", description = "Manages book-related operations such as viewing, adding, and deleting books.")
@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    // Endpoint to get all books
    @Operation(summary = "Get all books", description = "Allows a user to view all available books in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The list of books was successfully returned."),
            @ApiResponse(responseCode = "404", description = "No books found in the system.")
    })
    @GetMapping
    public ResponseEntity<?> getAllBooks() {
        List<Book> books = bookRepository.findAll();

        if (books.isEmpty()) {
            return new ResponseEntity<>(new ErrorResponse("No books found", "There are no books in the system."), HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    // Endpoint for adding a new book (only for authors)
    @Operation(summary = "Add a new book", description = "Allows an author to add a new book.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "The book was successfully created."),
            @ApiResponse(responseCode = "403", description = "User is not authorized to add a book (must be an author).")
    })
    @PostMapping("/add")
    public ResponseEntity<?> addBook(
            @Parameter(description = "The title of the book", example = "Book Title", required = true) @RequestParam String title,
            @Parameter(description = "The author of the book", example = "Book Author", required = true) @RequestParam String author,
            @Parameter(description = "The genre of the book", example = "Book Genre", required = true) @RequestParam String genre,
            @Parameter(description = "The year the book was published", example = "Year", required = true) @RequestParam int publishedYear,
            @Parameter(description = "The username of the user attempting to add the book", example = "larisamagdas", required = true) @RequestParam String username) {

        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty() || !user.get().getRole().equals("ROLE_AUTHOR")) {
            return new ResponseEntity<>(new ErrorResponse("Unauthorized", "Only authors are allowed to add books."), HttpStatus.FORBIDDEN);
        }

        Book newBook = new Book(title, author, genre, publishedYear, user.get()); // Legătura cu utilizatorul
        bookRepository.save(newBook);

        return new ResponseEntity<>(newBook, HttpStatus.CREATED);
    }

    // Endpoint for deleting a book (only by the author who added it)
    @Operation(summary = "Delete a book", description = "Allows an author to delete a book they have added.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The book was successfully deleted."),
            @ApiResponse(responseCode = "403", description = "User is not authorized to delete this book."),
            @ApiResponse(responseCode = "404", description = "Book not found.")
    })
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteBook(
            @Parameter(description = "The ID of the book to delete", example = "1", required = true) @PathVariable Long id,
            @Parameter(description = "The username of the user attempting to delete the book", example = "larisamagdas", required = true) @RequestParam String username) {

        Optional<Book> book = bookRepository.findById(id);
        if (book.isEmpty()) {
            return new ResponseEntity<>(new ErrorResponse("Book not found", "No book found with the provided ID."), HttpStatus.NOT_FOUND);
        }

        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty() || !user.get().getRole().equals("ROLE_AUTHOR")) {
            return new ResponseEntity<>(new ErrorResponse("Unauthorized", "Only authors are allowed to delete books."), HttpStatus.FORBIDDEN);
        }

        if (!book.get().getUser().getId().equals(user.get().getId())) {
            return new ResponseEntity<>(new ErrorResponse("Unauthorized", "You can only delete books you have added."), HttpStatus.FORBIDDEN);
        }

        bookRepository.deleteById(id);
        return new ResponseEntity<>("Book deleted successfully.", HttpStatus.OK);
    }

    // Endpoint to update book data
    @Operation(summary = "Update a book", description = "Allows an author to update the details of a book they have added.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The book was successfully updated."),
            @ApiResponse(responseCode = "403", description = "User is not authorized to update this book."),
            @ApiResponse(responseCode = "404", description = "Book not found.")
    })
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateBook(
            @Parameter(description = "The ID of the book to update", example = "1", required = true) @PathVariable Long id,
            @Parameter(description = "The new title of the book", example = "Updated Book Title", required = true) @RequestParam String title,
            @Parameter(description = "The new author of the book", example = "Updated Author", required = true) @RequestParam String author,
            @Parameter(description = "The new genre of the book", example = "Updated Genre", required = true) @RequestParam String genre,
            @Parameter(description = "The new year the book was published", example = "2025", required = true) @RequestParam int publishedYear,
            @Parameter(description = "The username of the user attempting to update the book", example = "larisamagdas", required = true) @RequestParam String username) {

        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty() || !user.get().getRole().equals("ROLE_AUTHOR")) {
            return new ResponseEntity<>(new ErrorResponse("Unauthorized", "Only authors are allowed to update books."), HttpStatus.FORBIDDEN);
        }

        Optional<Book> book = bookRepository.findById(id);
        if (book.isEmpty()) {
            return new ResponseEntity<>(new ErrorResponse("Book not found", "No book found with the provided ID."), HttpStatus.NOT_FOUND);
        }

        if (!book.get().getUser().getId().equals(user.get().getId())) {
            return new ResponseEntity<>(new ErrorResponse("Unauthorized", "You can only update books you have added."), HttpStatus.FORBIDDEN);
        }

        Book updatedBook = book.get();
        updatedBook.setTitle(title);
        updatedBook.setAuthor(author);
        updatedBook.setGenre(genre);
        updatedBook.setPublishedYear(publishedYear);

        bookRepository.save(updatedBook);

        return new ResponseEntity<>(updatedBook, HttpStatus.OK);
    }
}
