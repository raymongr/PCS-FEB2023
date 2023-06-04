package jfxcontrolescolar.modelo.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import jfxcontrolescolar.modelo.ConexionBD;
import jfxcontrolescolar.modelo.pojo.Carrera;
import jfxcontrolescolar.modelo.pojo.CarreraRespuesta;
import jfxcontrolescolar.utils.Constantes;


public class CarreraDAO {
    
    public static CarreraRespuesta obtenerCarrerasPorFacultad(int idFacultad){
        CarreraRespuesta respuesta = new CarreraRespuesta();
        respuesta.setCodigoRespuesta(Constantes.OPERACION_EXITOSA);
        ArrayList<Carrera> carreras = new ArrayList<>();
        Connection conexionBD = ConexionBD.abrirConexionBD();
        if(conexionBD != null){
            try {
                String consulta = "SELECT idCarrera, nombre, codigo FROM carrera "
                        + "WHERE idFacultad = ?";
                PreparedStatement prepararSentencia = conexionBD.prepareStatement(consulta);
                prepararSentencia.setInt(1, idFacultad);
                ResultSet resultado = prepararSentencia.executeQuery();
                while(resultado.next()){
                    Carrera carrera = new Carrera();
                    carrera.setIdCarrera(resultado.getInt("idCarrera"));
                    carrera.setNombre(resultado.getString("nombre"));
                    carrera.setCodigo(resultado.getString("codigo"));
                    carreras.add(carrera);
                }    
                conexionBD.close();
                respuesta.setCarreras(carreras);
            } catch (SQLException e) {
                e.printStackTrace();
                respuesta.setCodigoRespuesta(Constantes.ERROR_CONSULTA);
                System.err.println("Error consulta: "+e.getMessage());
            }    
        }else{
            respuesta.setCodigoRespuesta(Constantes.ERROR_CONEXION);
        } 
        return respuesta;
    }
    
}
