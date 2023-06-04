package jfxcontrolescolar.controladores;

import com.sun.javafx.scene.control.skin.TableHeaderRow;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import jfxcontrolescolar.JFXControlEscolar;
import jfxcontrolescolar.interfaz.INotificacionOperacion;
import jfxcontrolescolar.modelo.dao.AlumnoDAO;
import jfxcontrolescolar.modelo.pojo.Alumno;
import jfxcontrolescolar.modelo.pojo.AlumnoRespuesta;
import jfxcontrolescolar.utils.Constantes;
import jfxcontrolescolar.utils.Utilidades;


public class FXMLAlumnoAdminController implements Initializable, INotificacionOperacion {

    @FXML
    private TableView<Alumno> tvAlumnos;
    @FXML
    private TableColumn colMatricula;
    @FXML
    private TableColumn colNombre;
    @FXML
    private TableColumn colApPaterno;
    @FXML
    private TableColumn colApMaterno;
    @FXML
    private TableColumn colCorreo;
    @FXML
    private TableColumn colFacultad;
    @FXML
    private TableColumn colCarrera;
    @FXML
    private TextField tfBusqueda;
    
    private ObservableList<Alumno> alumnos;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
        cargarInformacionTabla();
    }

    private void configurarTabla(){
        colMatricula.setCellValueFactory(new PropertyValueFactory("matricula"));
        colNombre.setCellValueFactory(new PropertyValueFactory("nombre"));
        colApPaterno.setCellValueFactory(new PropertyValueFactory("apellidoPaterno"));
        colApMaterno.setCellValueFactory(new PropertyValueFactory("apellidoMaterno"));
        colCorreo.setCellValueFactory(new PropertyValueFactory("correo"));
        colFacultad.setCellValueFactory(new PropertyValueFactory("facultad"));
        colCarrera.setCellValueFactory(new PropertyValueFactory("carrera"));
        //colCarrera.impl_setReorderable(false);
        tvAlumnos.widthProperty().addListener(new ChangeListener<Number>(){
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                TableHeaderRow header = (TableHeaderRow) tvAlumnos.lookup("TableHeaderRow");
                header.reorderingProperty().addListener(new ChangeListener<Boolean>(){
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                        header.setReordering(false);
                    }
                });
            }
        });
        
    }

    private void cargarInformacionTabla(){
        alumnos = FXCollections.observableArrayList();
        AlumnoRespuesta respuestaBD = AlumnoDAO.obtenerInformacionAlumnos();
        switch(respuestaBD.getCodigoRespuesta()){
            case Constantes.ERROR_CONEXION:
                  Utilidades.mostrarDialogoSimple("Sin conexión", 
                          "Lo sentimos por el momento no hay conexión para poder cargar la información.", 
                          Alert.AlertType.ERROR);
                break;
            case Constantes.ERROR_CONSULTA:
                  Utilidades.mostrarDialogoSimple("Error al cargar los datos", 
                          "Hubo un error al cargar la información por favor inténtelo más tarde.", 
                          Alert.AlertType.WARNING);
                break;
            case Constantes.OPERACION_EXITOSA:
                  alumnos.addAll(respuestaBD.getAlumnos());
                  tvAlumnos.setItems(alumnos);
                  configurarBusquedaTabla();
                break;    
        }
    }

    @FXML
    private void clicBtnRegistrar(ActionEvent event) {
        irFormulario(false, null);
    }

    @FXML
    private void clicBtnEditar(ActionEvent event) {
        Alumno alumnoSeleccionado = tvAlumnos.getSelectionModel().getSelectedItem();
        if(alumnoSeleccionado != null){
            irFormulario(true, alumnoSeleccionado);
        }else{
            Utilidades.mostrarDialogoSimple("Selecciona un alumno", 
                    "Selecciona el registro en la tabla del alumno para su edición", 
                    Alert.AlertType.WARNING);
        }
    }
    
    private void irFormulario(boolean esEdicion, Alumno alumnoEdicion){
        //TODO
        try {
            FXMLLoader accesoControlador = new FXMLLoader
                (JFXControlEscolar.class.getResource("vistas/FXMLAlumnoFormulario.fxml"));
            Parent vista = accesoControlador.load();
            FXMLAlumnoFormularioController formulario = 
                                          accesoControlador.getController();

            formulario.inicializarInformacionFormulario(esEdicion, alumnoEdicion, this);

            Stage escenarioFormulario = new Stage();
            escenarioFormulario.setScene(new Scene(vista));
            escenarioFormulario.setTitle("Formulario");
            escenarioFormulario.initModality(Modality.APPLICATION_MODAL);
            escenarioFormulario.showAndWait();
        } catch (IOException ex) {
            Logger.getLogger(FXMLAlumnoAdminController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void configurarBusquedaTabla(){
        if(alumnos.size() > 0){
            FilteredList<Alumno> filtradoAlumnos = new FilteredList<>(alumnos, p -> true);
            tfBusqueda.textProperty().addListener(new ChangeListener<String>(){
                
                @Override
                public void changed(ObservableValue<? extends String> observable, 
                        String oldValue, String newValue) {
                    filtradoAlumnos.setPredicate(alumnoFiltro -> {
                        // Caso default o vacio
                        if(newValue == null || newValue.isEmpty()){
                            return true;
                        }
                        // Criterio de busqueda
                        String lowerNewValue = newValue.toLowerCase();
                        if(alumnoFiltro.getNombre().toLowerCase().contains(lowerNewValue)){
                            return true;
                        }else if(alumnoFiltro.getMatricula().toLowerCase().contains(lowerNewValue)){
                            return true;
                        }
                        return false;
                    });
                }
            
            });
            SortedList<Alumno> sortedListaAlumnos = new SortedList<>(filtradoAlumnos);
            sortedListaAlumnos.comparatorProperty().bind(tvAlumnos.comparatorProperty());
            tvAlumnos.setItems(sortedListaAlumnos);
        }
    }

    @FXML
    private void clicBtnEliminar(ActionEvent event) {
        Alumno alumnoSeleccionado = tvAlumnos.getSelectionModel().getSelectedItem();
        if(alumnoSeleccionado != null){
            boolean borrarRegistro = Utilidades.mostrarDialogoConfirmacion("Eliminar registro de alumno", 
                    "¿Estás seguro de que deseas elimar el registro del alumno(a): "
                            +alumnoSeleccionado.getNombre()+" "+alumnoSeleccionado.getApellidoPaterno()+"?");
            
            if(borrarRegistro){
                int codigoRespuesta = AlumnoDAO.eliminarAlumno(alumnoSeleccionado.getIdAlumno());
                switch(codigoRespuesta){
                    case Constantes.ERROR_CONEXION:
                            Utilidades.mostrarDialogoSimple("Error de conexión", 
                            "El alumno no pudo ser eliminado debido a un error en su conexión...", 
                            Alert.AlertType.ERROR);
                    break;
                    case Constantes.ERROR_CONSULTA:
                            Utilidades.mostrarDialogoSimple("Error al eliminar", 
                            "La información del alumno no puede ser eliminada, por favor intente más tarde.", 
                            Alert.AlertType.WARNING);
                    break;
                    case Constantes.OPERACION_EXITOSA:
                            Utilidades.mostrarDialogoSimple("Alumno eliminado", 
                            "La información del alumno fue eliminada correctamente", 
                            Alert.AlertType.INFORMATION);
                            cargarInformacionTabla();
                    break;
                }
            }
        }else{
            Utilidades.mostrarDialogoSimple("Selecciona un alumno", 
                    "Para eliminar un alumno debes seleccionarlo previamente de la tabla", 
                    Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void clicCerrarVentana(MouseEvent event) {
        Stage escenarioPrincipal = (Stage) tfBusqueda.getScene().getWindow();
        escenarioPrincipal.close();
    }

    @Override
    public void notificarOperacionGuardar(String nombreAlumno) {
        cargarInformacionTabla();
    }

    @Override
    public void notificarOperacionActualizar(String nombreAlumno) {
        cargarInformacionTabla();
    }

    @FXML
    private void clicExportarInformacion(MouseEvent event) {
        String fileHeader = "Matricula,Nombre,ApellidoPaterno,ApellidoMaterno,Correo,Carrera,Facultad\n";
        DirectoryChooser directorioSeleccion = new DirectoryChooser();
        File directorio = directorioSeleccion.showDialog(tfBusqueda.getScene().getWindow());
        if(directorio != null){
            try {
                String rutaArchivo = directorio.getAbsolutePath()+"/alumnos.csv";
                File archivoDescarga = new File(rutaArchivo);
                Writer escrituraArchivo = new BufferedWriter(new FileWriter(archivoDescarga));
                escrituraArchivo.write(fileHeader);
                for (Alumno alumno : alumnos) {
                    String fila = String.format("%s,%s,%s,%s,%s,%s,%s\n",Locale.US, alumno.getMatricula(),
                            alumno.getNombre(),alumno.getApellidoPaterno(), alumno.getApellidoMaterno(),
                            alumno.getCorreo(), alumno.getCarrera(), alumno.getFacultad());
                    escrituraArchivo.write(fila);
                }
                escrituraArchivo.close();
                Utilidades.mostrarDialogoSimple("Archivo exportado", 
                        "La información se exporto correctamente en el directorio "+rutaArchivo, 
                        Alert.AlertType.INFORMATION);
            } catch (IOException e) {
                Utilidades.mostrarDialogoSimple("Error al exportar", 
                        "Hubo un error al exportar la información, por favor inténtelo más tarde.", 
                        Alert.AlertType.ERROR);
            } 
        }
    }
 
}
