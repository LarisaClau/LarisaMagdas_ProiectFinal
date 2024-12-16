package com.example.MagdasLarisa_Project.Controllers;

import com.example.MagdasLarisa_Project.Models.ErrorResponse;
import com.example.MagdasLarisa_Project.Models.User;
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

@Tag(name = "Authentication Controller", description = "Handles user authentication, registration, and login operations.")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    // Endpoint to get all users
    @Operation(summary = "Get all users", description = "Returns a list of all users in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The list of users was successfully returned."),
            @ApiResponse(responseCode = "404", description = "No users found in the system.")
    })
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        List<User> users = userRepository.findAll();

        if (users.isEmpty()) {
            return new ResponseEntity<>(new ErrorResponse("No users found", "There are no users in the system"), HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    // Endpoint for user registration
    @Operation(summary = "Register a new user", description = "Allows a user to register with a username, email, password, and role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "The user was successfully created."),
            @ApiResponse(responseCode = "400", description = "There was an issue with the provided data (e.g., invalid role or existing username).")
    })
    @PostMapping("/register")
    public ResponseEntity<?> register(
            @Parameter(description = "The username of the user", example = "larisamagdas", required = true) @RequestParam String username,
            @Parameter(description = "The email of the user", example = "larisamagdas@example.com", required = true) @RequestParam String email,
            @Parameter(description = "The password of the user", example = "1234", required = true) @RequestParam String password,
            @Parameter(description = "The role of the user (ROLE_USER or ROLE_AUTHOR)", example = "ROLE_USER", required = true) @RequestParam String role) {

        // Check if the role is valid
        if (!role.equals("ROLE_USER") && !role.equals("ROLE_AUTHOR")) {
            return new ResponseEntity<>(new ErrorResponse("Invalid role", "Allowed roles are ROLE_USER and ROLE_AUTHOR"), HttpStatus.BAD_REQUEST);
        }

        // Check if the username already exists
        Optional<User> existingUser = userRepository.findByUsername(username);
        if (existingUser.isPresent()) {
            return new ResponseEntity<>(new ErrorResponse("Username already exists", "A user with this username already exists"), HttpStatus.BAD_REQUEST);
        }

        // Create a new user with the valid role
        User newUser = new User(username, email, password, role);
        userRepository.save(newUser);

        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }

    // Endpoint for user login
    @Operation(summary = "Login a user", description = "Allows a user to log in with their username and password.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful."),
            @ApiResponse(responseCode = "401", description = "Invalid username or password.")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Parameter(description = "The username of the user", example = "larisamagdas", required = true) @RequestParam String username,
            @Parameter(description = "The password of the user", example = "123", required = true) @RequestParam String password) {

        Optional<User> user = userRepository.findByUsername(username);

        if (user.isEmpty() || !user.get().getPassword().equals(password)) {
            // Return a response with the error message
            return new ResponseEntity<>(new ErrorResponse("Invalid credentials", "Username or password is incorrect"), HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>(user.get(), HttpStatus.OK);
    }

    // New endpoint to delete a user by ID
    // Secret code for deletion confirmation
    private static final String SECRET_CODE = "DELETE1234"; // This should be securely managed

    @Operation(summary = "Delete a user", description = "Deletes a user by their ID after confirming the secret code.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User was successfully deleted."),
            @ApiResponse(responseCode = "400", description = "Invalid or missing secret code."),
            @ApiResponse(responseCode = "404", description = "User not found.")
    })
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(
            @Parameter(description = "The ID of the user to delete", example = "1", required = true) @PathVariable Long id,
            @Parameter(description = "The secret code for confirmation", example = "DELETE123", required = true) @RequestParam String secretCode) {

        // Verify the secret code
        if (!SECRET_CODE.equals(secretCode)) {
            return new ResponseEntity<>(new ErrorResponse("Invalid secret code", "The provided secret code is incorrect."), HttpStatus.BAD_REQUEST);
        }

        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            return new ResponseEntity<>(new ErrorResponse("User not found", "No user found with the provided ID."), HttpStatus.NOT_FOUND);
        }

        userRepository.deleteById(id);

        return new ResponseEntity<>(new ErrorResponse("User deleted", "The user has been successfully deleted."), HttpStatus.OK);
    }


    // Endpoint to update user data

    @Operation(summary = "Update user details", description = "Allows a user to update their email and password. The old password must be provided for verification.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User details were successfully updated."),
            @ApiResponse(responseCode = "400", description = "Invalid data provided (e.g., empty email or password)."),
            @ApiResponse(responseCode = "404", description = "User not found with the provided username."),
            @ApiResponse(responseCode = "403", description = "The old password is incorrect.")
    })
    @PutMapping("/update")
    public ResponseEntity<?> updateUserDetails(
            @Parameter(description = "The username of the user whose details need to be updated", example = "larisamagdas", required = true)
            @RequestParam String username,

            @Parameter(description = "The old password of the user, which will be verified before updating details", example = "oldPassword123", required = true)
            @RequestParam String oldPassword,

            @Parameter(description = "The new email address of the user. This is optional and will only be updated if provided.", example = "newemail@example.com", required = false)
            @RequestParam(required = false) String newEmail,

            @Parameter(description = "The new password for the user. This is optional and will only be updated if provided. The password will be stored encrypted.", example = "newPassword123", required = false)
            @RequestParam(required = false) String newPassword) {

        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            return new ResponseEntity<>(new ErrorResponse("User not found", "No user found with the provided username."), HttpStatus.NOT_FOUND);
        }

        User user = userOptional.get();

        if (!user.getPassword().equals(oldPassword)) {
            return new ResponseEntity<>(new ErrorResponse("Invalid old password", "The provided old password is incorrect."), HttpStatus.FORBIDDEN);
        }

        if (newEmail != null && !newEmail.isEmpty()) {
            user.setEmail(newEmail);
        }

        if (newPassword != null && !newPassword.isEmpty()) {
            user.setPassword(newPassword);
        }

        userRepository.save(user);

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

}
