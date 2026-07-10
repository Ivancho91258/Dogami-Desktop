package com.iogami.dogami.controller;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent; //Detecta el click

public class MainController {

    @FXML
    public void initialize() {
        
        System.out.println("Controlador de DOgami inicializado");
    }

    @FXML
    public void seleccionarColor(MouseEvent event) {
        //Mensaje de prueba del click
        System.out.println("Color");
    }
}