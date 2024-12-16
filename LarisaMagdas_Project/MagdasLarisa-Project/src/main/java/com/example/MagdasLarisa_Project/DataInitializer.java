package com.example.MagdasLarisa_Project;

import com.example.MagdasLarisa_Project.Models.Book;
import com.example.MagdasLarisa_Project.Models.User;
import com.example.MagdasLarisa_Project.Repository.BookRepository;
import com.example.MagdasLarisa_Project.Repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public DataInitializer(BookRepository bookRepository, UserRepository userRepository) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        User user1 = new User("larisa1", "larisa1@yahoo.com", "123", "ROLE_USER");
        User user2 = new User("larisa2", "larisa2@gmail.com", "123", "ROLE_AUTHOR");

        userRepository.save(user1);
        userRepository.save(user2);

        Book book1 = new Book("Title1", "Author1", "Genre1", 2008, user2);
        Book book2 = new Book("Title2", "Author2", "Genre2", 2222, user2);

        bookRepository.save(book1);
        bookRepository.save(book2);
    }
}
