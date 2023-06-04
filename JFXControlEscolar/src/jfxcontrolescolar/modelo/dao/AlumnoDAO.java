package jfxcontrolescolar.modelo.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import jfxcontrolescolar.modelo.ConexionBD;
import jfxcontrolescolar.modelo.pojo.Alumno;
import jfxcontrolescolar.modelo.pojo.AlumnoRespuesta;
import jfxcontrolescolar.utils.Constantes;


public class AlumnoDAO {

    public static AlumnoRespuesta obtenerInformacionAlumnos(){
        AlumnoRespuesta respuesta = new AlumnoRespuesta();
        respuesta.setCodigoRespuesta(Constantes.OPERACION_EXITOSA);
        Connection conexionBD = ConexionBD.abrirConexionBD();
        if(conexionBD != null){
            try {
                String consulta = "SELECT  idAlumno, alumno.nombre, apellidoPaterno, apellidoMaterno, matricula, correo, " +
                        "fechaNacimiento, alumno.idCarrera, carrera.nombre AS nombreCarrera, " +
                        "carrera.idFacultad, facultad.nombre AS nombreFacultad, foto " +
                        "FROM alumno " +
                        "INNER JOIN carrera ON alumno.idCarrera = carrera.idCarrera " +
                        "INNER JOIN facultad ON carrera.idFacultad = facultad.idFacultad";
                PreparedStatement prepararSentencia = conexionBD.prepareStatement(consulta);
                ResultSet resultado = prepararSentencia.executeQuery();
                ArrayList<Alumno> alumnosConsulta = new ArrayList();
                while(resultado.next()){
                    Alumno alumno = new Alumno();
                    alumno.setIdAlumno(resultado.getInt("idAlumno"));
                    alumno.setNombre(resultado.getString("nombre"));
                    alumno.setApellidoPaterno(resultado.getString("apellidoPaterno"));
                    alumno.setApellidoMaterno(resultado.getString("apellidoMaterno"));
                    alumno.setFechaNacimiento(resultado.getString("fechaNacimiento"));
                    alumno.setIdCarrera(resultado.getInt("idCarrera"));
                    alumno.setCarrera(resultado.getString("nombreCarrera"));
                    alumno.setIdFacultad(resultado.getInt("idFacultad"));
                    alumno.setFacultad(resultado.getString("nombreFacultad"));
                    alumno.setCorreo(resultado.getString("correo"));
                    alumno.setMatricula(resultado.getString("matricula"));
                    alumno.setFoto(resultado.getBytes("foto"));
                    alumnosConsulta.add(alumno);
                }    
                respuesta.setAlumnos(alumnosConsulta);
                conexionBD.close();
            } catch (SQLException e) {
                respuesta.setCodigoRespuesta(Constantes.ERROR_CONSULTA);
            } 
        }else{
            respuesta.setCodigoRespuesta(Constantes.ERROR_CONEXION);
        }
        return respuesta;
    }
    
    public static int guardarAlumno(Alumno alumnoNuevo){
        int respuesta;
        Connection conexionBD = ConexionBD.abrirConexionBD();
        if(conexionBD != null){
            try {
                String sentencia = "INSERT INTO alumno (nombre, apellidoPaterno, apellidoMaterno, "
                                    + "matricula, correo, idCarrera, fechaNacimiento, foto) "+
                                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement prepararSentencia = conexionBD.prepareStatement(sentencia);
                prepararSentencia.setString(1, alumnoNuevo.getNombre());
                prepararSentencia.setString(2, alumnoNuevo.getApellidoPaterno());
                prepararSentencia.setString(3, alumnoNuevo.getApellidoMaterno());
                prepararSentencia.setString(4, alumnoNuevo.getMatricula());
                prepararSentencia.setString(5, alumnoNuevo.getCorreo());
                prepararSentencia.setInt(6, alumnoNuevo.getIdCarrera());
                prepararSentencia.setString(7, alumnoNuevo.getFechaNacimiento());
                prepararSentencia.setBytes(8, alumnoNuevo.getFoto());
                int filasAfectadas = prepararSentencia.executeUpdate();
                respuesta = (filasAfectadas == 1) ? Constantes.OPERACION_EXITOSA : Constantes.ERROR_CONSULTA;
                conexionBD.close();
            } catch (SQLException e) {
                respuesta = Constantes.ERROR_CONSULTA;
            }
        }else{
            respuesta = Constantes.ERROR_CONEXION;
        }
        return respuesta;
    }
    
    public static int modificarAlumno(Alumno alumnoEdicion){
        int respuesta;
        Connection conexionBD = ConexionBD.abrirConexionBD();
        if(conexionBD != null){
            try {
                String sentencia = "UPDATE alumno "
                        + "SET nombre = ?, apellidoPaterno = ?, apellidoMaterno = ?, correo = ?, "
                        + "idCarrera = ?, fechaNacimiento = ?, foto = ? "
                        + "WHERE idAlumno = ?";
                PreparedStatement prepararSentencia = conexionBD.prepareStatement(sentencia);
                prepararSentencia.setString(1, alumnoEdicion.getNombre());
                prepararSentencia.setString(2, alumnoEdicion.getApellidoPaterno());
                prepararSentencia.setString(3, alumnoEdicion.getApellidoMaterno());
                prepararSentencia.setString(4, alumnoEdicion.getCorreo());
                prepararSentencia.setInt(5, alumnoEdicion.getIdCarrera());
                prepararSentencia.setString(6, alumnoEdicion.getFechaNacimiento());
                prepararSentencia.setBytes(7, alumnoEdicion.getFoto());
                prepararSentencia.setInt(8, alumnoEdicion.getIdAlumno());
                int filasAfectadas = prepararSentencia.executeUpdate();
                respuesta = (filasAfectadas == 1) ? Constantes.OPERACION_EXITOSA : Constantes.ERROR_CONSULTA;
                conexionBD.close();
            } catch (SQLException e) {
                respuesta = Constantes.ERROR_CONSULTA;
            }
        }else{
            respuesta = Constantes.ERROR_CONEXION;
        }
        return respuesta;
    }
    
    public static int eliminarAlumno(int idAlumno){
        int respuesta;
        Connection conexionBD = ConexionBD.abrirConexionBD();
        if(conexionBD != null){
            try {
                String sentencia = "DELETE FROM alumno "
                        + "WHERE idAlumno = ?";
                PreparedStatement prepararSentencia = conexionBD.prepareStatement(sentencia);
                prepararSentencia.setInt(1, idAlumno);
                int filasAfectadas = prepararSentencia.executeUpdate();
                respuesta = (filasAfectadas == 1) ? Constantes.OPERACION_EXITOSA : Constantes.ERROR_CONSULTA;
                conexionBD.close();
            } catch (SQLException e) {
                respuesta = Constantes.ERROR_CONSULTA;
            }
        }else{
            respuesta = Constantes.ERROR_CONEXION;
        }
        return respuesta;
    }
}
