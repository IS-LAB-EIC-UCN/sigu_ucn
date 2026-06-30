package cl.ucn.app.model;

import jakarta.persistence.*;

/**
 * Mapeada a "categorias_ticket" según 01_scheme.sql.
 */
@Entity
@Table(name = "categorias_ticket")
public class CategoriaTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_categoria", nullable = false, length = 64)
    private String nombreCategoria;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenido;

    public CategoriaTicket() {}

    public CategoriaTicket(String nombreCategoria, String contenido) {
        this.nombreCategoria = nombreCategoria;
        this.contenido = contenido;
    }

    public Long getId() { return id; }

    public String getNombreCategoria() { return nombreCategoria; }
    public void setNombreCategoria(String nombreCategoria) { this.nombreCategoria = nombreCategoria; }

    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }
}
