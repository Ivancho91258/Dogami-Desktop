package com.iogami.dogami;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        // Cargamos el archivo FXML desde la carpeta de recursos usando una ruta absoluta
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/iogami/dogami/view/main-view.fxml"));
        
        Scene scene = new Scene(fxmlLoader.load());

        String cssPath = Main.class.getResource("/com/iogami/dogami/view/estilos.css").toExternalForm();
        scene.getStylesheets().add(cssPath);

        primaryStage.setTitle("DOgami - Área de Trabajo");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}