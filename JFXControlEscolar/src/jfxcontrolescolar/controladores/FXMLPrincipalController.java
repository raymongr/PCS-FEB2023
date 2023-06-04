/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jfxcontrolescolar.controladores;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import jfxcontrolescolar.JFXControlEscolar;
import jfxcontrolescolar.utils.Utilidades;

/**
 *
 * @author raymon
 */
public class FXMLPrincipalController implements Initializable {

    @FXML
    private Pane menu;
    @FXML
    private ImageView imgMenu;
    private boolean menuAbierto;
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        menuAbierto = false;
    }    

    @FXML
    private void clicOpenMenu(MouseEvent event) {
        if(menuAbierto)
            actualizaEstadoMenu(-255, false, "recursos/menu.png");
        else
            actualizaEstadoMenu(255, true, "recursos/close.png");
    }

    @FXML
    private void clicIrAdminAlumnos(MouseEvent event) {
        actualizaEstadoMenu(-255, false, "recursos/menu.png");
        Stage escenarioAlumnos = new Stage();
        escenarioAlumnos.setScene(Utilidades.inicializarEscena("vistas/FXMLAlumnoAdmin.fxml"));
        escenarioAlumnos.setTitle("Alumnos");
        escenarioAlumnos.initModality(Modality.APPLICATION_MODAL);
        escenarioAlumnos.showAndWait();
    }
    
    private void actualizaEstadoMenu(int posicion, boolean abierto, String icono){
        animacionMenu(posicion);
        menuAbierto = abierto;
        imgMenu.setImage(new Image(JFXControlEscolar.class.getResource(icono).toString()));
    }
    
    private void animacionMenu(int posicion){
        TranslateTransition translate = new TranslateTransition();
        translate.setNode(menu);
        translate.setDuration(Duration.millis(300));
        translate.setByX(posicion);
        translate.setAutoReverse(true);
        translate.play();
    }

    @FXML
    private void clicCerrarSesion(MouseEvent event) {
        Stage escenarioPrincipal = (Stage) imgMenu.getScene().getWindow();
        escenarioPrincipal.setScene(Utilidades.inicializarEscena("vistas/FXMLInicioSesion.fxml"));
        escenarioPrincipal.setTitle("Iniciar sesión");
        escenarioPrincipal.show();
    }
}
