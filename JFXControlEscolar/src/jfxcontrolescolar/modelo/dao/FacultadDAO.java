package jfxcontrolescolar.modelo.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import jfxcontrolescolar.modelo.ConexionBD;
import jfxcontrolescolar.modelo.pojo.Facultad;
import jfxcontrolescolar.modelo.pojo.FacultadRespuesta;
import jfxcontrolescolar.utils.Constantes;


public class FacultadDAO {
    
    public static FacultadRespuesta obtenerInformacionFacultades(){
        FacultadRespuesta respuesta = new FacultadRespuesta();
        respuesta.setCodigoRespuesta(Constantes.OPERACION_EXITOSA);
        ArrayList<Facultad> facultades = new ArrayList<>();
        Connection conexionBD = ConexionBD.abrirConexionBD();
        if(conexionBD != null){
            try {
                String consulta = "SELECT * FROM facultad ";
                PreparedStatement prepararSentencia = conexionBD.prepareStatement(consulta);
                ResultSet resultado = prepararSentencia.executeQuery();
                while(resultado.next()){
                    Facultad facultad = new Facultad();
                    facultad.setIdFacultad(resultado.getInt("idFacultad"));
                    facultad.setNombre(resultado.getString("nombre"));
                    facultades.add(facultad);
                }    
                conexionBD.close();
                respuesta.setFacultades(facultades);
            } catch (SQLException e) {
                respuesta.setCodigoRespuesta(Constantes.ERROR_CONSULTA);
                System.err.println("Error consulta: "+e.getMessage());
            }    
        }else{
            respuesta.setCodigoRespuesta(Constantes.ERROR_CONEXION);
        } 
        return respuesta;
    }
        
}
