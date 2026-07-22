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
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TitledPane;

public class MainController {

    // Enlace TabPane principal de Scene Builder
    @FXML private TabPane tabPaneProyectos;

    //Paneles de propiedades
    @FXML private TitledPane panelPropiedadesLinea;
    @FXML private TitledPane panelPropiedadesPoligono;
    @FXML private TitledPane panelPropiedadesElipse;

    //Propiedades de las líneas
    @FXML private ComboBox<String> comboTipoLinea;
    @FXML private ColorPicker colorPickerLinea;
    @FXML private Spinner<Double> spinnerGrosorLinea;
    @FXML private Spinner<Double> spinnerLongitudLinea;
    @FXML private ComboBox<String> comboAnguloLinea;

    //Propiedades de los polígonos
    @FXML private Spinner<Integer> spinnerLadosPoligono;
    @FXML private ColorPicker colorPickerBordePoligono;
    @FXML private ColorPicker colorPickerRellenoPoligono;
    @FXML private Spinner<Double> spinnerGrosorPoligono;
    @FXML private Spinner<Double> spinnerTamanoPoligono;
    @FXML private ComboBox<String> comboTipoLineaPoligono;
    @FXML private ComboBox<String> comboAnguloPoligono;
    
    //Propiedades de las elipses / circulos
    @FXML private CheckBox checkCirculoPerfecto;
    @FXML private ColorPicker colorPickerBordeElipse;
    @FXML private ColorPicker colorPickerRellenoElipse;
    @FXML private Spinner<Double> spinnerGrosorElipse;

    //Enlaces botones de las herramientas
    @FXML private ToggleButton btnHerramientaLinea;
    @FXML private ToggleButton btnHerramientaPoligono;
    @FXML private ToggleButton btnHerramientaCirculo;

    @FXML private javafx.scene.shape.Ellipse elipseActual;

    private int contadorProyectos = 1; //Se nombran las Ventanas, 1, 2, 3, 4..
    private double inicioX, inicioY;
    private Line lineaActual;

    private javafx.scene.shape.Polygon poligonoActual;


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

            //Propiedades de los Polígonos
            poligonoActual.setStroke(colorPickerBordePoligono.getValue());
            poligonoActual.setStrokeWidth(spinnerGrosorPoligono.getValue());
            poligonoActual.setFill(colorPickerRellenoPoligono.getValue());

            //Esquinas redondeadas en los poligonos, mejora el diseño
            poligonoActual.setStrokeLineJoin(javafx.scene.shape.StrokeLineJoin.ROUND);
            poligonoActual.setStrokeLineCap(javafx.scene.shape.StrokeLineCap.ROUND);

            String tipoLineaP = comboTipoLineaPoligono.getValue();
            
            if ("Pliegue en Valle".equals(tipoLineaP)){
                poligonoActual.getStrokeDashArray().addAll(10d, 10d);

            } else if ("Pliegue en Montaña".equals(tipoLineaP)) {
                poligonoActual.getStrokeDashArray().addAll(20d, 5d, 3d, 5d);

            } else if ("Rayos X".equals(tipoLineaP)){
                poligonoActual.getStrokeDashArray().addAll(2d, 5d);
            }

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

            //Propiedades de las Elipses / Círculos
            elipseActual.setStroke(colorPickerBordeElipse.getValue());
            elipseActual.setStrokeWidth(spinnerGrosorElipse.getValue());
            elipseActual.setFill(colorPickerRellenoElipse.getValue());

            lienzo.getChildren().add(elipseActual);

        }
    
    }
    
    private void arrastrarDibujo(MouseEvent event){
        double actualX = event.getX();
        double actualY = event.getY();

        //Conversión de 96 DPI: 1 mm = 3.78 px
        double pxPorMm = 3.78;

        //TRAZO DE LA LÍNEA
        if (btnHerramientaLinea != null && btnHerramientaLinea.isSelected() && lineaActual != null){
            double dx = actualX - inicioX;
            double dy = actualY - inicioY;
            double anguloCalculado = Math.atan2(dy, dx);
            double distanciaPx = Math.hypot(dx, dy);

            //Evaluar la restricción del ángulo seleccionado

            String anguloSeleccionado = comboAnguloLinea.getValue();
            if (anguloSeleccionado != null && !"Libre".equals(anguloSeleccionado)){
                double anguloRestriccion = 0;

                //Conversión de angulos a radianes
                if ("22.5°".equals(anguloSeleccionado)) anguloRestriccion = Math.toRadians(22.5);
                else if ("45°".equals(anguloSeleccionado)) anguloRestriccion = Math.toRadians(45);
                else if ("67.5°".equals(anguloSeleccionado)) anguloRestriccion = Math.toRadians(67.5);
                else if ("90°".equals(anguloSeleccionado)) anguloRestriccion = Math.toRadians(90);

                //Se fuerza matemáticamente el ángulo más cercano
                long multiplo = Math.round(anguloCalculado / anguloRestriccion);
                anguloCalculado = multiplo * anguloRestriccion;
            }
            //Aplicamos las coordenadas definitivas a la línea
            lineaActual.setEndX(inicioX + distanciaPx * Math.cos(anguloCalculado));
            lineaActual.setEndY(inicioY + distanciaPx * Math.sin(anguloCalculado));

            //Actualiza el spinner en tiempo real
            double distanciaMm = Math.round((distanciaPx / pxPorMm) * 10.0) / 10.0;
            spinnerLongitudLinea.getValueFactory().setValue(distanciaMm);

        }

        //Trazo del polígono

        else if (btnHerramientaPoligono != null && btnHerramientaPoligono.isSelected()  && poligonoActual != null){
            //Cálculo de las distancia / radio desde el click inicial hasta los bordes
            double radioPx = Math.hypot(actualX - inicioX, actualY - inicioY);
            double anguloInicial = Math.atan2(actualY - inicioY, actualX - inicioX);

            //Lógica de los ángulos
            String anguloSeleccionado = comboAnguloPoligono.getValue();
            if (anguloSeleccionado != null && !"Libre".equals(anguloSeleccionado)){
                double anguloRestriccion = 0;

            //Conversión de angulos a radianes
                if ("22.5°".equals(anguloSeleccionado)) anguloRestriccion = Math.toRadians(22.5);
                else if ("45°".equals(anguloSeleccionado)) anguloRestriccion = Math.toRadians(45);

            //Se fuerza matemáticamente el ángulo más cercano
            long multiplo = Math.round(anguloInicial / anguloRestriccion);
            anguloInicial = multiplo * anguloRestriccion;

            }
            //Leer los números dinámicos del spinner
            int lados = spinnerLadosPoligono.getValue();
            Double[] puntos = new Double[lados * 2];

            //Matemáticas para generar las esquinas del polígono regular

            for (int i = 0; i < lados; i++){
                double angulo = anguloInicial + i * (2 * Math.PI / lados);
                puntos[i * 2] = inicioX + radioPx * Math.cos(angulo); //Coordenadas X
                puntos[i * 2 + 1] = inicioY + radioPx * Math.sin(angulo); //Coordenadas Y

            }

            poligonoActual.getPoints().setAll(puntos);

            //Actualizar el spinner en tiempo real
            double radioMm = Math.round((radioPx / pxPorMm) * 10.0) / 10.0;
            spinnerTamanoPoligono.getValueFactory().setValue(radioMm);

        }

        //Trazo del circulo / elipse
        else if (btnHerramientaCirculo != null && btnHerramientaCirculo.isSelected() && elipseActual != null){

            //Radio absoluto
                   
            double radioX = Math.abs(actualX - inicioX);
            double radioY = Math.abs(actualY - inicioY);

            if (checkCirculoPerfecto != null && checkCirculoPerfecto.isSelected()){

                double radioMax = Math.max(radioX, radioY);
                radioX = radioMax;
                radioY = radioMax;
            }
            //Horizontal Radio X, Vertical Radio Y, Math.abs para tener el radio siempre positivo
            elipseActual.setRadiusX(radioX);
            elipseActual.setRadiusY(radioY);

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

        //PROPIEDADES LINEA
        comboTipoLinea.getItems().addAll("Solida","Pliegue en Valle","Pliegue en Montaña","Rayos X");
        comboTipoLinea.getSelectionModel().selectFirst();

        comboAnguloLinea.getItems().addAll("Libre", "22.5°", "45°", "67.5°", "90°");
        comboAnguloLinea.getSelectionModel().selectFirst();

        //Grosor de la linea
        SpinnerValueFactory<Double> factory = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.5, 20.0, 1.5, 0.5);
        spinnerGrosorLinea.setValueFactory(factory);

        //Longitud de la linea
        spinnerLongitudLinea.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(1.0, 500.0, 50.0, 1.0));;
        
        colorPickerLinea.setValue(Color.BLACK);

        //PROPIEDADES POLÍGONOS
        comboTipoLineaPoligono.getItems().addAll("Solido", "Pliegue en Valle", "Pliegue en Montaña", "Rayos X");
        comboTipoLineaPoligono.getSelectionModel().selectFirst();

        comboAnguloPoligono.getItems().addAll("Libre", "22.5°", "45°");
        comboAnguloPoligono.getSelectionModel().selectFirst();

        //Lados Polígono
        spinnerLadosPoligono.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(3, 20, 4));

        //Grosos borde
        spinnerGrosorPoligono.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0.5, 20.0, 1.5, 0.5));

        //Tamaño Polígono
        spinnerTamanoPoligono.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(1.0, 500.0, 1.0));

        //Colores Polígono
        colorPickerBordePoligono.setValue(Color.BLACK);
        colorPickerRellenoPoligono.setValue(Color.TRANSPARENT);

        //PROPIEDADES ELIPSE / CÍRCULO
        //Grosor Borde
        spinnerGrosorElipse.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0.5, 20.0, 1.5, 0.5));

        //Color Elipse / Córculo
        colorPickerBordeElipse.setValue(Color.BLACK);
        colorPickerRellenoElipse.setValue(Color.TRANSPARENT);

        //LÓGICA VISIBILIDAD PANELES DE PROPIEDADES

        //Panle Línea
        panelPropiedadesLinea.visibleProperty().bind(btnHerramientaLinea.selectedProperty());
        panelPropiedadesLinea.managedProperty().bind(panelPropiedadesLinea.visibleProperty());
        
        //Panel Polígono
        panelPropiedadesPoligono.visibleProperty().bind(btnHerramientaPoligono.selectedProperty());
        panelPropiedadesPoligono.managedProperty().bind(panelPropiedadesPoligono.visibleProperty());

        //Panel Elipse / Circulo
        panelPropiedadesElipse.visibleProperty().bind(btnHerramientaCirculo.selectedProperty());
        panelPropiedadesElipse.managedProperty().bind(panelPropiedadesElipse.visibleProperty());

        //Se eliminan las pestañas fantasma
        tabPaneProyectos.getTabs().clear();

        //Simulación de nuevo proyecto, para que cargue las propiedades correctas
        crearNuevoProyecto(null);
    }

}