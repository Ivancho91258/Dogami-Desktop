package com.iogami.dogami.controller;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent; //Detecta el click

public class MainController {

    @FXML
    public void initialize() {
        
        System.out.println("¡Controlador de DOgami inicializado con éxito!");
    }

    @FXML
    public void seleccionarColor(MouseEvent Event) {
        //Mensaje de prueba del click
        System.out.println("Color");
    }
}