package cl.ucn.app.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "recursos")
public class Recurso {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "stock", nullable = false)
    private int stock;

    @Column(name = "tipo", nullable = false, length = 30)
    private String tipo;

    public Recurso() {}
    
    public Recurso(String nombre, int stock, String tipo) {
        this.nombre = nombre;
        this.stock = stock;
        this.tipo = tipo;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public int getStock() {
        return stock;
    }

    public String getTipo() {
        return tipo;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public void getTipo(String tipo) {
        this.tipo = tipo;
    }
    @Override
    public String toString() {
        return "Recurso [id=" + id + ", nombre=" + nombre + ", stock=" + stock + ", tipo=" + tipo + "]";
    }
    
}
