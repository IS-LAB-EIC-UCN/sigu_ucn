package cl.ucn.app.model;

import jakarta.persistence.*;

@Entity
@Table(name = "espacios")
public class Espacio {
    @Id
    private String id;
    
    @Column(nullable = false)
    private String nombre;
    
    @Column(nullable = false)
    private String tipo;
    
    @Column(nullable = false)
    private int capacidad;
    
    @Column(nullable = false)
    private boolean disponible;

    public Espacio() {}

    public Espacio(String id, String nombre, String tipo, int capacidad, boolean disponible) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.capacidad = capacidad;
        this.disponible = disponible;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public int getCapacidad() { return capacidad; }
    public void setCapacidad(int capacidad) { this.capacidad = capacidad; }

    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }
}
