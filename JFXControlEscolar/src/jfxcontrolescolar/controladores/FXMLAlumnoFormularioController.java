/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jfxcontrolescolar.controladores;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import jfxcontrolescolar.interfaz.INotificacionOperacion;
import jfxcontrolescolar.modelo.dao.AlumnoDAO;
import jfxcontrolescolar.modelo.dao.CarreraDAO;
import jfxcontrolescolar.modelo.dao.FacultadDAO;
import jfxcontrolescolar.modelo.pojo.Alumno;
import jfxcontrolescolar.modelo.pojo.Carrera;
import jfxcontrolescolar.modelo.pojo.CarreraRespuesta;
import jfxcontrolescolar.modelo.pojo.Facultad;
import jfxcontrolescolar.modelo.pojo.FacultadRespuesta;
import jfxcontrolescolar.utils.Constantes;
import jfxcontrolescolar.utils.Utilidades;


public class FXMLAlumnoFormularioController implements Initializable {

    @FXML
    private Label lbTitulo;
    @FXML
    private TextField tfMatricula;
    @FXML
    private TextField tfNombre;
    @FXML
    private TextField tfApellidoPaterno;
    @FXML
    private TextField tfApellidoMaterno;
    @FXML
    private TextField tfCorreo;
    @FXML
    private ComboBox<Facultad> cbFacultad;
    @FXML
    private ComboBox<Carrera> cbCarrera;
    @FXML
    private ImageView ivFotoAlumno;
    @FXML
    private DatePicker dpFechaNacimiento;
    
    private ObservableList<Facultad> facultades;
    private ObservableList<Carrera> carreras;
    private Alumno alumnoEdicion;
    private boolean esEdicion;
    private INotificacionOperacion interfazNotificacion;
    private File archivoFoto;
    String estiloError = "-fx-border-color: RED; -fx-border-width: 2; -fx-border-radius: 2;";
    String estiloNormal = "-fx-border-width: 0;";
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarInformacionFacultad();
        cbFacultad.valueProperty().addListener(new ChangeListener<Facultad>(){
            
            @Override
            public void changed(ObservableValue<? extends Facultad> observable, Facultad oldValue, Facultad newValue) {
                if(newValue != null){
                    cargarInformacionCarrera(newValue.getIdFacultad());
                }
            }
            
        });
        dpFechaNacimiento.setEditable(false);
    }

    public void inicializarInformacionFormulario(boolean esEdicion, 
            Alumno alumnoEdicion, INotificacionOperacion interfazNotificion){
        this.esEdicion = esEdicion;
        this.alumnoEdicion = alumnoEdicion;
        this.interfazNotificacion = interfazNotificion;
        // TO DO 
        if(esEdicion){
            lbTitulo.setText("Editar información del alumno(a) "+alumnoEdicion.getNombre());
            cargarInformacionEdicion();
        }else{
            lbTitulo.setText("Registrar nuevo alumno");
        }
    }
    
    private void cargarInformacionEdicion(){
        tfMatricula.setText(alumnoEdicion.getMatricula());
        tfMatricula.setEditable(false);
        tfNombre.setText(alumnoEdicion.getNombre());
        tfApellidoPaterno.setText(alumnoEdicion.getApellidoPaterno());
        tfApellidoMaterno.setText(alumnoEdicion.getApellidoMaterno());
        tfCorreo.setText(alumnoEdicion.getCorreo());
        dpFechaNacimiento.setValue(LocalDate.parse(alumnoEdicion.getFechaNacimiento()));
        int posicionFacultad = obtenerPosicionComboFacultad(alumnoEdicion.getIdFacultad());
        cbFacultad.getSelectionModel().select(posicionFacultad);
        int posicionCarrera = obtenerPosicionComboCarrera(alumnoEdicion.getIdCarrera());
        cbCarrera.getSelectionModel().select(posicionCarrera);
        
        try{
            ByteArrayInputStream inputFoto = new ByteArrayInputStream(alumnoEdicion.getFoto());
            Image imgFotoAlumno = new Image(inputFoto);
            ivFotoAlumno.setImage(imgFotoAlumno);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @FXML
    private void clicBtnGuardar(ActionEvent event) {
        validarCamposRegistro();
    }

    @FXML
    private void clicBtnCancelar(ActionEvent event) {
        cerrarVentana();
    }
    
    @FXML
    private void clicBtnSeleccionarFoto(ActionEvent event) {
        FileChooser dialogoImagen = new FileChooser();
        dialogoImagen.setTitle("Selecciona una foto");
        FileChooser.ExtensionFilter filtroImg = new FileChooser.ExtensionFilter("Archivos JPG (*.jpg)", "*.JPG");
        dialogoImagen.getExtensionFilters().add(filtroImg);
        Stage escenarioActual = (Stage) tfNombre.getScene().getWindow();
        archivoFoto = dialogoImagen.showOpenDialog(escenarioActual);
        
        if(archivoFoto != null){
            try {
                BufferedImage bufferImg = ImageIO.read(archivoFoto);
                Image imagenFoto = SwingFXUtils.toFXImage(bufferImg, null);
                ivFotoAlumno.setImage(imagenFoto);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    private void validarCamposRegistro(){
        establecerEstiloNormal();
        boolean datosValidos = true;
        
        String matricula = tfMatricula.getText();
        String nombre = tfNombre.getText();
        String apellidoPaterno = tfApellidoPaterno.getText();
        String apellidoMaterno = tfApellidoMaterno.getText();
        String correo = tfCorreo.getText();
        LocalDate fechaNacimiento = dpFechaNacimiento.getValue();
        int posicionFacultad = cbFacultad.getSelectionModel().getSelectedIndex();
        int posicionCarrera = cbCarrera.getSelectionModel().getSelectedIndex();
        
        if(matricula.length() != 9){
            tfMatricula.setStyle(estiloError);
            datosValidos = false;
        }
        
        if(nombre.isEmpty()){
            tfNombre.setStyle(estiloError);
            datosValidos = false;
        }
        
        if(apellidoPaterno.isEmpty()){
            tfApellidoPaterno.setStyle(estiloError);
            datosValidos = false;
        }
        
        if (apellidoMaterno.isEmpty()) {
            tfApellidoMaterno.setStyle(estiloError);
            datosValidos = false;
        }
        
        if(!Utilidades.correoValido(correo)){
            tfCorreo.setStyle(estiloError);
            datosValidos = false;
        }
        
        if(fechaNacimiento == null){
            dpFechaNacimiento.setStyle(estiloError);
            datosValidos = false;
        }
        
        if(posicionFacultad == -1){
            cbFacultad.setStyle(estiloError);
            datosValidos = false;
        }
        
        if(posicionCarrera == -1){
            cbCarrera.setStyle(estiloError);
            datosValidos = false;
        }
        
        if(datosValidos){
            Alumno alumnoValidado = new Alumno();
            alumnoValidado.setNombre(nombre);
            alumnoValidado.setApellidoPaterno(apellidoPaterno);
            alumnoValidado.setApellidoMaterno(apellidoMaterno);
            alumnoValidado.setMatricula(matricula);
            alumnoValidado.setCorreo(correo);
            alumnoValidado.setFechaNacimiento(fechaNacimiento.toString());
            alumnoValidado.setIdCarrera(carreras.get(posicionCarrera).getIdCarrera());

            try{
                
                if(esEdicion){
                    
                    if(archivoFoto != null || alumnoEdicion.getFoto().length > 0){
                        if(archivoFoto != null){
                            alumnoValidado.setFoto(Files.readAllBytes(archivoFoto.toPath()));
                        }else{
                            alumnoValidado.setFoto(alumnoEdicion.getFoto());
                        }
                        alumnoValidado.setIdAlumno(alumnoEdicion.getIdAlumno());
                        actualizarAlumno(alumnoValidado);
                    }else{
                        Utilidades.mostrarDialogoSimple("Selecciona una imagen", 
                                "Para editar el registro del alumno debes seleccionar su foto desde la opción Seleccionar Foto.", 
                                Alert.AlertType.WARNING);
                    }
                }else{
                    // File -> byte[] 
                    if(archivoFoto != null){
                        alumnoValidado.setFoto(Files.readAllBytes(archivoFoto.toPath()));
                        registrarAlumno(alumnoValidado);
                    }else{
                        Utilidades.mostrarDialogoSimple("Selecciona una imagen", 
                                "Para guardar el registro del alumno debes seleccionar su foto desde la opción Seleccionar Foto.", 
                                Alert.AlertType.WARNING);
                    }
                    
                }
                
            }catch(IOException e){
                Utilidades.mostrarDialogoSimple("Error con el archivo", 
                        "Hubo un error al intentar guardar la imagen, por favor vuelva a seleccionar el archivo", 
                        Alert.AlertType.ERROR);
            }
        }
    }
    
    private void registrarAlumno(Alumno alumnoRegistro){
        int codigoRespuesta = AlumnoDAO.guardarAlumno(alumnoRegistro);
        switch(codigoRespuesta){
            case Constantes.ERROR_CONEXION:
                Utilidades.mostrarDialogoSimple("Error de conexión", 
                        "El alumno no pudo ser guardado debido a un error en su conexión...", 
                        Alert.AlertType.ERROR);
                break;
            case Constantes.ERROR_CONSULTA:
                Utilidades.mostrarDialogoSimple("Error en la información", 
                        "La información del alumno no puede ser guardada, por favor verifique su información e intente más tarde.", 
                        Alert.AlertType.WARNING);
                break;
            case Constantes.OPERACION_EXITOSA:
                Utilidades.mostrarDialogoSimple("Alumno registrado", 
                        "La información del alumno fue guardada correctamente", 
                        Alert.AlertType.INFORMATION);
                cerrarVentana();
                interfazNotificacion.notificarOperacionGuardar(alumnoRegistro.getNombre());
                break;
        }
    }
    
    private void actualizarAlumno(Alumno alumnoActualizar){
        int codigoRespuesta = AlumnoDAO.modificarAlumno(alumnoActualizar);
        switch(codigoRespuesta){
            case Constantes.ERROR_CONEXION:
                Utilidades.mostrarDialogoSimple("Error de conexión", 
                        "El alumno no pudo ser actualizado debido a un error en su conexión...", 
                        Alert.AlertType.ERROR);
                break;
            case Constantes.ERROR_CONSULTA:
                Utilidades.mostrarDialogoSimple("Error en la información", 
                        "La información del alumno no puede ser actualizada, por favor verifique su información e intente más tarde.", 
                        Alert.AlertType.WARNING);
                break;
            case Constantes.OPERACION_EXITOSA:
                Utilidades.mostrarDialogoSimple("Alumno actualizado", 
                        "La información del alumno fue actualizada correctamente", 
                        Alert.AlertType.INFORMATION);
                cerrarVentana();
                interfazNotificacion.notificarOperacionActualizar(alumnoActualizar.getNombre());
                break;
        }
    }
    
    private void cargarInformacionFacultad(){
        facultades = FXCollections.observableArrayList();
        FacultadRespuesta facultadesBD = FacultadDAO.obtenerInformacionFacultades();
        switch(facultadesBD.getCodigoRespuesta()){
            case Constantes.ERROR_CONEXION:
                  Utilidades.mostrarDialogoSimple("Error de conexión", 
                            "Error en la conexión con la base de datos.", 
                            Alert.AlertType.ERROR);
                break;
            case Constantes.ERROR_CONSULTA:
                  Utilidades.mostrarDialogoSimple("Error de consulta", 
                          "Por el momento no se puede obtener la información", 
                          Alert.AlertType.INFORMATION);
                break;
            case Constantes.OPERACION_EXITOSA:
                  facultades.addAll(facultadesBD.getFacultades());
                  cbFacultad.setItems(facultades);
                break;
        }
    }
    
    private void cargarInformacionCarrera(int idFacultad){
        carreras = FXCollections.observableArrayList();
        CarreraRespuesta carrerasBD = CarreraDAO.obtenerCarrerasPorFacultad(idFacultad);
        switch(carrerasBD.getCodigoRespuesta()){
            case Constantes.ERROR_CONEXION:
                  Utilidades.mostrarDialogoSimple("Error de conexión", 
                            "Error en la conexión con la base de datos.", 
                            Alert.AlertType.ERROR);
                break;
            case Constantes.ERROR_CONSULTA:
                  Utilidades.mostrarDialogoSimple("Error de consulta", 
                          "Por el momento no se puede obtener la información", 
                          Alert.AlertType.INFORMATION);
                break;
            case Constantes.OPERACION_EXITOSA:
                  carreras.addAll(carrerasBD.getCarreras());
                  cbCarrera.setItems(carreras);
                break;
        }
    }
    
    private void cerrarVentana(){
        Stage escenarioBase = (Stage) lbTitulo.getScene().getWindow();
        escenarioBase.close();
    }
    
    private int obtenerPosicionComboFacultad(int idFacultad){
        for (int i = 0; i < facultades.size(); i++) {
            if(facultades.get(i).getIdFacultad() == idFacultad)
                return i;
        }
        return 0;
    }
    
    private int obtenerPosicionComboCarrera(int idCarrera){
        for (int i = 0; i < carreras.size(); i++) {
            if(carreras.get(i).getIdCarrera() == idCarrera)
                return i;
        }
        return 0;
    }

    private void establecerEstiloNormal(){
        tfMatricula.setStyle(estiloNormal);
        tfNombre.setStyle(estiloNormal);
        tfApellidoPaterno.setStyle(estiloNormal);
        tfApellidoMaterno.setStyle(estiloNormal);
        tfCorreo.setStyle(estiloNormal);
        dpFechaNacimiento.setStyle(estiloNormal);
        cbFacultad.setStyle(estiloNormal);
        cbCarrera.setStyle(estiloNormal);
    }
}
