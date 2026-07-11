package com.iogami.dogami.controller;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent; //Detecta el click
import javafx.event.ActionEvent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;
import java.util.Optional;
import javafx.beans.value.ObservableValue;
import javafx.scene.shape.Line;
import javafx.scene.paint.Color;
import javafx.scene.control.ToggleButton;

public class MainController {

    // Enlace TabPane principal de Scene Builder
    @FXML
    private TabPane tabPaneProyectos;

    @FXML
    private ToggleButton btnHerramientaLinea;

    private int contadorProyectos = 1; //Se nombran las Ventanas, 1, 2, 3, 4..
    private double inicioX, inicioY;
    private Line lineaActual;


    @FXML
    public void crearNuevoProyecto(ActionEvent event){
        //Se crea la pestaña principal del proyecto
        Tab tabProyecto = new Tab("Proyecto " + contadorProyectos);

        BorderPane borderPaneInterno = new BorderPane();

        //Se crea el contenido interno del proyecto
        TabPane tabPanePaginas = new TabPane();
        tabPanePaginas.setSide(javafx.geometry.Side.BOTTOM); //pestañas abajo
        tabPanePaginas.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        //Se inicia la memoria del contador de las pestañas
        tabPanePaginas.getProperties().put("contadorPaginas", 1);
        
        //Creamos la página 1 por defecto
        Tab pagina1 = crearNuevaPagina("Página 1");

        //Botón "+"
        Tab tabBotonMas = new Tab("+");
        tabBotonMas.setStyle("-fx-font-weight: Bold; -fx-font-size: 14px;");
        
        //Ensamblamos páginas y botón "+"
        tabPanePaginas.getTabs().addAll(pagina1, tabBotonMas);

        //Detección del botón
        tabPanePaginas.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Tab> observable, Tab viejaPestana, Tab nuevaPestana) -> {
            if (nuevaPestana == tabBotonMas){
                int contadorActual = (int) tabPanePaginas.getProperties().get("contadorPaginas");
                int nuevoNumero = contadorActual + 1;

                //Creación de la página continuaal número
                Tab nuevaPag = crearNuevaPagina("Página " + nuevoNumero);

                //mantenemos la nueva página antes del botón "+"
                tabPanePaginas.getTabs().add(tabPanePaginas.getTabs().size() - 1, nuevaPag);
                tabPanePaginas.getSelectionModel().select(nuevaPag);

                //Guardamos el nuevo número de página en la memoria
                tabPanePaginas.getProperties().put("contadorPaginas", nuevoNumero);
            }
        });
        borderPaneInterno.setCenter(tabPanePaginas);
        tabProyecto.setContent(borderPaneInterno);
        tabPaneProyectos.getTabs().add(tabProyecto);
        tabPaneProyectos.getSelectionModel().select(tabProyecto);

        contadorProyectos++;

    }

    //Creación de la página nueva
    private Tab crearNuevaPagina(String titulo){
        Tab tabPagina = new Tab(titulo);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setStyle("-fx-background: #E0E0E0;");

        Pane lienzoDibujo = new Pane();
        lienzoDibujo.setPrefSize(816, 1056);

        lienzoDibujo.setStyle("-fx-background-color: white;");

        lienzoDibujo.setOnMousePressed(this::iniciarDibujo);
        lienzoDibujo.setOnMouseDragged(this::arrastrarDibujo);
        lienzoDibujo.setOnMouseReleased(this::finalizarDibujo);

        scrollPane.setContent(lienzoDibujo);
        tabPagina.setContent(scrollPane);
        
        //opciones click derecho de la página
        ContextMenu menuOpciones = new ContextMenu();

        //Renombrar
        MenuItem opcionRenombrar = new MenuItem("Renombrar");
        opcionRenombrar.setOnAction(e -> {
            TextInputDialog dialogo = new TextInputDialog(tabPagina.getText());
            dialogo.setTitle("Renombrar Página");
            dialogo.setHeaderText(null);
            dialogo.setContentText("Nuevo nombre:");
            Optional<String> resultado = dialogo.showAndWait();
            
            //se cambia el nombre
            resultado.ifPresent(nombre -> tabPagina.setText(nombre));
        });

        //Duplicar
        MenuItem opcionDuplicar = new MenuItem("Duplicar");
        opcionDuplicar.setOnAction(e -> {
            TabPane contenedorPadre = tabPagina.getTabPane();
            Tab pestañaCopia = crearNuevaPagina(tabPagina.getText() + " (Copia)");
            //Se insterda al lado de la página original
            contenedorPadre.getTabs().add(contenedorPadre.getTabs().indexOf(tabPagina) + 1, pestañaCopia);
            contenedorPadre.getSelectionModel().select(pestañaCopia);
        });

        //Cerrar
        MenuItem opcionCerrar = new MenuItem("Cerrar");
        opcionCerrar.setOnAction(e -> {
            TabPane contenedorPadre = tabPagina.getTabPane();
            //Evitamos que se cierre la última página, debe tener al menos una abierta
            if (contenedorPadre.getTabs().size() > 2){
                contenedorPadre.getTabs().remove(tabPagina);
            } else {
                System.out.println("No se puede cerrar la última página.");
            }
        });

        //Se agregan las opciones a los menus
        menuOpciones.getItems().addAll(opcionRenombrar, opcionDuplicar, opcionCerrar);
        tabPagina.setContextMenu(menuOpciones);
        return tabPagina;
    }

    private void iniciarDibujo(MouseEvent event){

        //Verificación del botón linea
        if (btnHerramientaLinea == null || !btnHerramientaLinea.isSelected()) {
            return;
        }

        //Se guardan las coordenadas iniciales
        inicioX = event.getX();
        inicioY = event.getY();

        lineaActual = new Line(inicioX, inicioY, inicioX, inicioY);
        lineaActual.setStroke(Color.BLACK);
        lineaActual.setStrokeWidth(2.0);

        Pane lienzo = (Pane) event.getSource();
        lienzo.getChildren().add(lineaActual);

    }

    private void arrastrarDibujo(MouseEvent event){
        if (lineaActual != null){
            lineaActual.setEndX(event.getX());
            lineaActual.setEndY(event.getY());

        }
    }

    private void finalizarDibujo(MouseEvent event){
        lineaActual = null;
    }

    @FXML
    public void initialize() {
        
        System.out.println("Controlador de DOgami inicializado");

        //Se eliminan las pestañas fantasma
        tabPaneProyectos.getTabs().clear();

        //Simulación de nuevo proyecto, para que cargue las propiedades correctas
        crearNuevoProyecto(null);
    }

    @FXML
    public void seleccionarColor(MouseEvent event) {
        //Mensaje de prueba del click
        System.out.println("Color");
    }
}