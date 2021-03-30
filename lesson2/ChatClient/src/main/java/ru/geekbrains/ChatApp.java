package ru.geekbrains;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ChatApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    private static Scene scene;
    private static Stage ourStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        ourStage = primaryStage;
        scene = new Scene(loadFxml("/chat.fxml"), 600, 800);
        primaryStage.setTitle("My chat");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private static Parent loadFxml(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ru.geekbrains.ChatApp.class.getResource(fxml));
        return fxmlLoader.load();
    }

    public static Stage getStage() {
        return ourStage;
    }

}
