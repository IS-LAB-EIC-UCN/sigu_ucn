package cl.ucn.app.model;

import jakarta.persistence.*;

@Entity
@Table(name = "categoria_ticket")
public class CategoriaTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_categoria", nullable = false)
    private String nombreCategoria;

    @Column(name = "contenido", nullable = false)
    private String contenido;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombreCategoria() { return nombreCategoria; }
    public void setNombreCategoria(String n) { this.nombreCategoria = n; }
    public String getContenido() { return contenido; }
    public void setContenido(String c) { this.contenido = c; }
}