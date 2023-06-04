package jfxcontrolescolar.modelo.pojo;

import java.util.ArrayList;


public class AlumnoRespuesta {
    
    private int codigoRespuesta;
    private ArrayList<Alumno> alumnos;

    public AlumnoRespuesta() {
    }

    public AlumnoRespuesta(int codigoRespuesta, ArrayList<Alumno> alumnos) {
        this.codigoRespuesta = codigoRespuesta;
        this.alumnos = alumnos;
    }

    public int getCodigoRespuesta() {
        return codigoRespuesta;
    }

    public void setCodigoRespuesta(int codigoRespuesta) {
        this.codigoRespuesta = codigoRespuesta;
    }

    public ArrayList<Alumno> getAlumnos() {
        return alumnos;
    }

    public void setAlumnos(ArrayList<Alumno> alumnos) {
        this.alumnos = alumnos;
    }
    
    
}
