/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jfxcontrolescolar.controladores;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;
import jfxcontrolescolar.JFXControlEscolar;
import jfxcontrolescolar.modelo.dao.SesionDAO;
import jfxcontrolescolar.modelo.pojo.Usuario;
import jfxcontrolescolar.utils.Constantes;
import jfxcontrolescolar.utils.Utilidades;

/**
 * FXML Controller class
 *
 * @author raymon
 */
public class FXMLInicioSesionController implements Initializable {

    @FXML
    private TextField tfUsuario;
    @FXML
    private Label lbErrorUsuario;
    @FXML
    private Label lbErrorPassword;
    @FXML
    private PasswordField tfPassword;
    @FXML
    private ImageView ivLogo;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        iniciarAnimacion();
    }    

    @FXML
    private void clicIniciarSesion(ActionEvent event) {
        lbErrorUsuario.setText("");
        lbErrorPassword.setText("");
        validarCampos();
    }
    
    private void validarCampos(){
       String usuario = tfUsuario.getText();
       String password = tfPassword.getText();
       boolean sonValidos = true;
       if(usuario.isEmpty()){
           sonValidos = false;
           lbErrorUsuario.setText("El campo Usuario es requerido.");
       }
       if(password.length() == 0){
           sonValidos = false;
           lbErrorPassword.setText("El campo Contraseña en requerido.");
       }
       if(sonValidos)
           validarCredencialesUsuario(usuario, password);
     
    }
    
    private void validarCredencialesUsuario(String usuario, String password){
        Usuario usuarioRespuesta = SesionDAO.verificarUsuarioSesion(usuario, password);
        switch(usuarioRespuesta.getCodigoRespuesta()){
            case Constantes.ERROR_CONEXION:
                Utilidades.mostrarDialogoSimple("Error de conexión", 
                        "Por el momento no hay conexión, por favor inténtelo más tarde.", 
                        Alert.AlertType.ERROR);
                break;
            case Constantes.ERROR_CONSULTA:
                Utilidades.mostrarDialogoSimple("Error en la solicitud", 
                        "Por el momento no se puede procesar la solicitud de verificación.", 
                        Alert.AlertType.ERROR);
                break;
            case Constantes.OPERACION_EXITOSA:
                if(usuarioRespuesta.getIdUsuario() > 0){
                   Utilidades.mostrarDialogoSimple("Usuario verificado", 
                           "Bienvenido(a) "+usuarioRespuesta.toString()+" al sistema...", 
                           Alert.AlertType.INFORMATION);
                   irPantallaPrincipal();
                }else{
                    Utilidades.mostrarDialogoSimple("Credenciales incorrectas", 
                         "El usuario y/o contraseña no son correctos, por favor verifica la información", 
                         Alert.AlertType.WARNING);
                }
                break;
            default:
                Utilidades.mostrarDialogoSimple("Error de petición", 
                        "El sistema no esta disponible por el momento.", Alert.AlertType.ERROR);
        }
    }
    
    private void irPantallaPrincipal(){                    
        Stage escenarioBase = (Stage) tfUsuario.getScene().getWindow();
        escenarioBase.setScene(Utilidades.inicializarEscena("vistas/FXMLPrincipal.fxml"));
        escenarioBase.setTitle("Home");
        escenarioBase.setResizable(false);
        escenarioBase.show();
    }
    
    private void iniciarAnimacion(){
        TranslateTransition translate = new TranslateTransition();
        translate.setNode(ivLogo);
        translate.setDuration(Duration.millis(1300));
        translate.setByX(330);
        translate.setAutoReverse(true);
        translate.play();
    }
}
