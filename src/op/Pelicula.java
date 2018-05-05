package op;

import java.util.Vector;

public class Pelicula {
    
    private int id; 
    private String titulo; 
    private Vector actores; 
    private String genero; 
    private int ano; 

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }
    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public Vector getActores() {
        return actores;
    }

    public void setActores(Vector actores) {
        this.actores = actores;
    }
    
}
