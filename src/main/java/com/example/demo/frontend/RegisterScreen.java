package com.example.demo.frontend;

import com.example.demo.dto.UserDTO;
import com.example.demo.service.UserService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;

public class RegisterScreen {
    private final UserService userService;
    private final ApplicationContext applicationContext;
    private Label messageLabel;
    private Button registerButton;

    public RegisterScreen(UserService userService, ApplicationContext applicationContext) {
        this.userService = userService;
        this.applicationContext = applicationContext;
    }

    public void show(Stage stage) {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        // Title
        Label titleLabel = new Label("Register New Account");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        grid.add(titleLabel, 0, 0, 2, 1);

        // First Name
        Label firstNameLabel = new Label("First Name:");
        grid.add(firstNameLabel, 0, 1);
        TextField firstNameField = new TextField();
        grid.add(firstNameField, 1, 1);

        // Last Name
        Label lastNameLabel = new Label("Last Name:");
        grid.add(lastNameLabel, 0, 2);
        TextField lastNameField = new TextField();
        grid.add(lastNameField, 1, 2);

        // Email
        Label emailLabel = new Label("Email:");
        grid.add(emailLabel, 0, 3);
        TextField emailField = new TextField();
        grid.add(emailField, 1, 3);

        // Password
        Label passwordLabel = new Label("Password:");
        grid.add(passwordLabel, 0, 4);
        PasswordField passwordField = new PasswordField();
        grid.add(passwordField, 1, 4);

        // Confirm Password
        Label confirmPasswordLabel = new Label("Confirm Password:");
        grid.add(confirmPasswordLabel, 0, 5);
        PasswordField confirmPasswordField = new PasswordField();
        grid.add(confirmPasswordField, 1, 5);

        // Register Button
        registerButton = new Button("Register");
        registerButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        
        // Back to Login Button
        Button backButton = new Button("Back to Login");

        // Button container
        GridPane buttonContainer = new GridPane();
        buttonContainer.setHgap(10);
        buttonContainer.add(registerButton, 0, 0);
        buttonContainer.add(backButton, 1, 0);
        grid.add(buttonContainer, 1, 6);

        // Message Label
        messageLabel = new Label();
        grid.add(messageLabel, 1, 7);

        // Register Button Action
        registerButton.setOnAction(e -> {
            try {
                registerButton.setDisable(true);
                messageLabel.setText("Registering...");
                messageLabel.setStyle("-fx-text-fill: black;");

                // Get form values
                String firstName = firstNameField.getText().trim();
                String lastName = lastNameField.getText().trim();
                String email = emailField.getText().trim();
                String password = passwordField.getText();
                String confirmPassword = confirmPasswordField.getText();

                // Validate input
                if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || 
                    password.isEmpty() || confirmPassword.isEmpty()) {
                    showError("Please fill in all fields");
                    return;
                }

                if (!isValidEmail(email)) {
                    showError("Please enter a valid email address");
                    return;
                }

                if (password.length() < 6) {
                    showError("Password must be at least 6 characters long");
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    showError("Passwords do not match");
                    return;
                }

                // Create UserDTO
                UserDTO userDTO = new UserDTO();
                userDTO.setFirstName(firstName);
                userDTO.setLastName(lastName);
                userDTO.setEmail(email);
                userDTO.setPassword(password);

                // Register user
                userService.registerUser(userDTO);

                // Show success message
                showSuccess("Registration successful! Please login.");

                // Clear form
                firstNameField.clear();
                lastNameField.clear();
                emailField.clear();
                passwordField.clear();
                confirmPasswordField.clear();

                // Return to login screen after short delay
                Thread.sleep(1500);
                LoginScreen loginScreen = new LoginScreen(userService, applicationContext);
                loginScreen.show(stage);

            } catch (RuntimeException ex) {
                showError(ex.getMessage());
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            } finally {
                registerButton.setDisable(false);
            }
        });

        // Back Button Action
        backButton.setOnAction(e -> {
            LoginScreen loginScreen = new LoginScreen(userService, applicationContext);
            loginScreen.show(stage);
        });

        Scene scene = new Scene(grid, 500, 400);
        stage.setTitle("Register New Account");
        stage.setScene(scene);
        stage.show();
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    private void showError(String message) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-text-fill: red;");
    }

    private void showSuccess(String message) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-text-fill: green;");
    }
} 