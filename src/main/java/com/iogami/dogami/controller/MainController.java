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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TitledPane;

public class MainController {

    // Enlace TabPane principal de Scene Builder
    @FXML
    private TabPane tabPaneProyectos;

    @FXML
    private ComboBox<String> comboTipoLinea;

    @FXML
    private Spinner<Double> spinnerGrosorLinea;

    @FXML
    private ColorPicker colorPickerLinea;

    @FXML
    private TitledPane panelPropiedadesLinea;

    @FXML
    private ToggleButton btnHerramientaLinea;

    @FXML
    private ToggleButton btnHerramientaPoligono;

    @FXML
    private ToggleButton btnHerramientaCirculo;

    private javafx.scene.shape.Ellipse elipseActual;

    private int contadorProyectos = 1; //Se nombran las Ventanas, 1, 2, 3, 4..
    private double inicioX, inicioY;
    private Line lineaActual;

    private javafx.scene.shape.Polygon poligonoActual;
    private int ladoPoligono = 4;


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
        //Centrar el lienzo
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        //Configuración del tamaño del lienzo
        Pane lienzoDibujo = new Pane();
        lienzoDibujo.setPrefSize(816, 1056);
        lienzoDibujo.setMaxSize(816, 1056);

        lienzoDibujo.setStyle("-fx-background-color: white;");

        //Clipping, evitara trazos fuera del lienzo
        javafx.scene.shape.Rectangle mascaraRecorte = new javafx.scene.shape.Rectangle(816, 1056);
        lienzoDibujo.setClip(mascaraRecorte);

        //-----------------

        lienzoDibujo.setOnMousePressed(this::iniciarDibujo);
        lienzoDibujo.setOnMouseDragged(this::arrastrarDibujo);
        lienzoDibujo.setOnMouseReleased(this::finalizarDibujo);

        //Grupo para mantener el lienzo de un tamaño fijo:
        javafx.scene.Group grupoEnvoltorio = new javafx.scene.Group(lienzoDibujo);

        //StackPane para centrar el lienzo
        javafx.scene.layout.StackPane contenedorCentrado = new javafx.scene.layout.StackPane(grupoEnvoltorio);
        contenedorCentrado.setStyle("-fx-backgroun-color: #E0E0E0;");

        //Se guarda el StackPane en el ScrollPane
        scrollPane.setContent(contenedorCentrado);
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
            //Se crea la nueva pestaña
            Tab pestañaCopia = crearNuevaPagina(tabPagina.getText() + " (Copia)");

            //Extraemos el contenido de la página para duplicarlo
            ScrollPane scrollOriginal = (ScrollPane) tabPagina.getContent();
            Pane lienzoOriginal = (Pane) scrollOriginal.getContent();

            ScrollPane scrollCopia = (ScrollPane) pestañaCopia.getContent ();
            Pane lienzoCopia = (Pane) scrollCopia.getContent();
            
            //Recorremos los vectores dibujados en el lienzo original
            for (javafx.scene.Node nodo : lienzoOriginal.getChildren()) {

                //verificamos si el trazo es una linea
                if (nodo instanceof Line) {
                    Line lineaOriginal = (Line) nodo;
                    //Creamos la nueva linea con las mismas coordenadas
                    Line cloneLine = new Line (
                        lineaOriginal.getStartX(),
                        lineaOriginal.getStartY(),
                        lineaOriginal.getEndX(),
                        lineaOriginal.getEndY()
                    );

                    //Duplicamos las propiedades de las lineas
                    cloneLine.setStroke(lineaOriginal.getStroke());
                    cloneLine.setStrokeWidth(lineaOriginal.getStrokeWidth());
                    cloneLine.getStrokeDashArray().addAll(lineaOriginal.getStrokeDashArray());

                    //Se añade el clon de las lineas a la nueva página
                    lienzoCopia.getChildren().add(cloneLine);
                }
            }
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

        //Se guardan las coordenadas iniciales
        inicioX = event.getX();
        inicioY = event.getY();

        Pane lienzo = (Pane) event.getSource();
        
        //Lógica de la linea
        if (btnHerramientaLinea != null && btnHerramientaLinea.isSelected()){
            lineaActual = new Line(inicioX, inicioY, inicioX, inicioY);
            lineaActual.setStroke(colorPickerLinea.getValue());
            lineaActual.setStrokeWidth(spinnerGrosorLinea.getValue());

            String tipoLinea = comboTipoLinea.getValue();

            if ("Pliegue en Valle".equals(tipoLinea)){
                lineaActual.getStrokeDashArray().addAll(10d, 10d);

            } else if ("Pliegue en Montaña".equals(tipoLinea)) {
                lineaActual.getStrokeDashArray().addAll(20d, 5d, 3d, 5d);
            } else if ("Rayos X".equals(tipoLinea)){
                lineaActual.getStrokeDashArray().addAll(2d, 5d);
            }

            lienzo.getChildren().add(lineaActual);

        }

        //Lógica de los polígonos
        else if (btnHerramientaPoligono != null && btnHerramientaPoligono.isSelected()){
            poligonoActual = new javafx.scene.shape.Polygon();

            //Polígono base, bordes visibles y fondo transparente

            poligonoActual.setStroke(javafx.scene.paint.Color.BLACK);
            poligonoActual.setStrokeWidth(2.0);
            poligonoActual.setFill(javafx.scene.paint.Color.WHITE);

            lienzo.getChildren().add(poligonoActual);
        }

        //Lógica de los circulos / elipses
        else if (btnHerramientaCirculo != null && btnHerramientaCirculo.isSelected()){
            elipseActual = new javafx.scene.shape.Ellipse();

            //Asigna el centro del circulo al primer click
            elipseActual.setCenterX(inicioX);
            elipseActual.setCenterY(inicioY);

            //Inicia en radio 0, hasta que se arrastra
            elipseActual.setRadiusX(0);
            elipseActual.setRadiusY(0);

            //Propiedades iniciales
            elipseActual.setStroke(javafx.scene.paint.Color.BLACK);
            elipseActual.setStrokeWidth(2.0);
            elipseActual.setFill(javafx.scene.paint.Color.WHITE);

            lienzo.getChildren().add(elipseActual);

        }
    
    }
    
    private void arrastrarDibujo(MouseEvent event){
        
        //Trazo de la linea
        if (btnHerramientaLinea != null && btnHerramientaLinea.isSelected() && lineaActual != null){
            lineaActual.setEndX(event.getX());
            lineaActual.setEndY(event.getY());

        }

        //Trazo del polígono

        else if (btnHerramientaPoligono != null && btnHerramientaPoligono.isSelected()  && poligonoActual != null){
            double actualX = event.getX();
            double actualY = event.getY();

            //Cálculo de las distancia / radio desde el click inicial hasta los bordes
            double radio = Math.hypot(actualX - inicioX, actualY - inicioY);

            //Cálculo de los angulos para permitir rotar el cuadrado mientras se dibuja
            double anguloInicial = Math.atan2(actualY - inicioY, actualX - inicioX);

            Double[] puntos = new Double[ladoPoligono * 2];

            //Matemáticas para generar las esquinas del polígono regular

            for (int i = 0; i < ladoPoligono; i++){
                double angulo = anguloInicial + i * (2 * Math.PI / ladoPoligono);
                puntos[i * 2] = inicioX + radio * Math.cos(angulo); //Coordenadas X
                puntos[i * 2 + 1] = inicioY + radio * Math.sin(angulo); //Coordenadas Y

            }

            //Se actualizan los puntos del polígono en el lienzo
            poligonoActual.getPoints().setAll(puntos);

        }

        //Trazo del circulo / elipse
        else if (btnHerramientaCirculo != null && btnHerramientaCirculo.isSelected() && elipseActual != null){
            double actualX = event.getX();
            double actualY = event.getY();

            //Horizontal Radio X, Vertical Radio Y, Math.abs para tener el radio siempre positivo
            elipseActual.setRadiusX(Math.abs(actualX - inicioX));
            elipseActual.setRadiusY(Math.abs(actualY - inicioY));

        }

    }

    private void finalizarDibujo(MouseEvent event){
        if (btnHerramientaLinea != null && btnHerramientaLinea.isSelected()){
            lineaActual = null;
        } else if (btnHerramientaPoligono != null && btnHerramientaPoligono.isSelected()){
            poligonoActual = null;
        } else if (btnHerramientaCirculo != null && btnHerramientaCirculo.isSelected()){
            elipseActual = null;
        }
    }

    @FXML
    public void initialize() {
        
        System.out.println("Controlador de DOgami inicializado");

        //Propiedades de la linea
        comboTipoLinea.getItems().addAll("Solida","Pliegue en Valle","Pliegue en Montaña","Rayos X");
        comboTipoLinea.getSelectionModel().selectFirst();

        //Configuración de los tamaños del spinner
        SpinnerValueFactory<Double> factory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.5, 20.0, 2.0, 0.5);
        spinnerGrosorLinea.setValueFactory(factory);

        colorPickerLinea.setValue(Color.BLACK);

        //Código para mostrar el panel de propiedades solo cuando se selecciona la linea

        panelPropiedadesLinea.visibleProperty().bind(btnHerramientaLinea.selectedProperty());
        panelPropiedadesLinea.managedProperty().bind(panelPropiedadesLinea.visibleProperty());
    
        //Se eliminan las pestañas fantasma
        tabPaneProyectos.getTabs().clear();

        //Simulación de nuevo proyecto, para que cargue las propiedades correctas
        crearNuevoProyecto(null);
    }

}