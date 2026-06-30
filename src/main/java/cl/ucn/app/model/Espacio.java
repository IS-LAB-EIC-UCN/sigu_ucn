package cl.ucn.app.model;

import jakarta.persistence.*;

@Entity
@Table(name = "espacios")
public class Espacio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 50)
    private String tipo;

    @Column(nullable = false)
    private Integer capacidad;

    @Column(nullable = false)
    private Boolean disponible = true;

    public Espacio() {
    }

    public Espacio(String nombre, String tipo, Integer capacidad, Boolean disponible) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.capacidad = capacidad;
        this.disponible = disponible;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public Integer getCapacidad() {
        return capacidad;
    }

    public Boolean getDisponible() {
        return disponible;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setCapacidad(Integer capacidad) {
        this.capacidad = capacidad;
    }

    public void setDisponible(Boolean disponible) {
        this.disponible = disponible;
    }

    public void setId(Long id) {
        this.id = id;
    }
}