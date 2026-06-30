package org.example.moodtracker;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.moodtracker.controller.LoginController;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // 1. Показать окно входа
        if (!showLoginDialog()) {
            return; // Пользователь закрыл окно без входа
        }

        // 2. Загрузить главное окно
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/moodtracker/fxml/main-view.fxml"));
        VBox root = loader.load();

        Scene scene = new Scene(root, 1200, 700);

        String cssUrl = getClass().getResource("/org/example/moodtracker/css/style.css").toExternalForm();
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl);
        }

        primaryStage.setTitle("Mood Tracker - Дневник самочувствия");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private boolean showLoginDialog() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/moodtracker/fxml/login-view.fxml"));
        VBox loginRoot = loader.load();

        Stage loginStage = new Stage();
        loginStage.setTitle("Вход в Mood Tracker");
        loginStage.setScene(new Scene(loginRoot, 350, 400));
        loginStage.initModality(Modality.APPLICATION_MODAL);
        loginStage.setResizable(false);
        loginStage.showAndWait();

        LoginController controller = loader.getController();
        return controller.isAuthenticated();
    }

    public static void main(String[] args) {
        launch(args);
    }
}