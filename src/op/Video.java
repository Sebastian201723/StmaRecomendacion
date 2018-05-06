/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package op;

import java.util.Vector;

/**
 *
 * @author alejandralandinez
 */
public class Video {
     
    private int id; 
    private String nombre; 
    private Vector competencia;  
    private double puntuacion; 

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(double puntuacion) {
        this.puntuacion = puntuacion;
    }

    public Vector getCompetencia() {
        return competencia;
    }

    public void setCompetencia(Vector competencia) {
        this.competencia = competencia;
    }
   
}
