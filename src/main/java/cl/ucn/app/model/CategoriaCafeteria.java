package cl.ucn.app.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "categorias_cafeteria")
public class CategoriaCafeteria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String nombre;

    public CategoriaCafeteria() {}

    public CategoriaCafeteria(String nombre) {
        this.nombre = nombre;}

    public Long getId() {
        return id;}

    public String getNombre() {
        return nombre;}

    public void setId(Long id) {
        this.id = id;}

    public void setNombre(String nombre) {
        this.nombre = nombre;}
}