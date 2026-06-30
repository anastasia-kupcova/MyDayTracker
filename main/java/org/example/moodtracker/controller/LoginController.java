package org.example.moodtracker.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.moodtracker.dao.UserDAO;
import org.example.moodtracker.database.DatabaseConnection;
import org.example.moodtracker.model.User;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Button registerButton;
    @FXML private Button guestButton;

    private UserDAO userDAO = new UserDAO();
    private boolean authenticated = false;

    @FXML
    private void initialize() {
        loginButton.setOnAction(e -> handleLogin());
        registerButton.setOnAction(e -> handleRegister());
        guestButton.setOnAction(e -> handleGuest());
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Ошибка", "Введите логин и пароль");
            return;
        }

        User user = userDAO.authenticate(username, password);
        if (user != null) {
            DatabaseConnection.setCurrentUser(user);
            authenticated = true;
            closeWindow();
        } else {
            showAlert("Ошибка", "Неверный логин или пароль");
        }
    }

    private void handleRegister() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Ошибка", "Введите логин и пароль для регистрации");
            return;
        }

        if (userDAO.usernameExists(username)) {
            showAlert("Ошибка", "Пользователь с таким логином уже существует");
            return;
        }

        boolean success = userDAO.register(username, password);
        if (success) {
            showInfo("Успех", "Пользователь '" + username + "' зарегистрирован! Теперь войдите.");
        } else {
            showAlert("Ошибка", "Не удалось зарегистрировать");
        }
    }

    private void handleGuest() {
        User guest = userDAO.getGuestUser();
        if (guest != null) {
            DatabaseConnection.setCurrentUser(guest);
            authenticated = true;
            closeWindow();
        } else {
            showAlert("Ошибка", "Не удалось загрузить гостевой доступ");
        }
    }

    private void closeWindow() {
        Stage stage = (Stage) loginButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public boolean isAuthenticated() {
        return authenticated;
    }
}