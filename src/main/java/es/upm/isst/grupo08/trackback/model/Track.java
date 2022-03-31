package es.upm.isst.grupo08.trackback.model;

import javax.persistence.Entity;
import javax.persistence.Id;


@Entity
public class Track {
    @Id
    private String nombre;
    private int status;
    
 
    public Track( String nombre, int status ) {
   
        this.nombre = nombre;
   
 
        this.status = status;
  
    }
    public Track () { }
 
    
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
  
    
 
    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }
  
}
