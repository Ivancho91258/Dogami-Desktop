package com.iogami.dogami;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Un panel básico vacío
        StackPane root = new StackPane();
        
        // Creamos la escena principal
        Scene scene = new Scene(root, 1024, 768);

        primaryStage.setTitle("DOgami - Área de Trabajo");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}