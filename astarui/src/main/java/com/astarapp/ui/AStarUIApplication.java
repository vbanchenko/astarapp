package com.astarapp.ui;

import java.io.InputStream;
import java.util.Objects;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AStarUIApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        try (InputStream fxmlStream = getClass().getClassLoader().getResourceAsStream("fxml/main.fxml")) {
            FXMLLoader loader = new FXMLLoader();
            loader.load(Objects.requireNonNull(fxmlStream));
            Parent root = loader.getRoot();
            primaryStage.setTitle(UIConfig.WINDOW_TITLE);
            primaryStage.setScene(new Scene(root));
            primaryStage.setHeight(UIConfig.FIELD_SIZE_IN_PIXELS);
            primaryStage.show();
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}
