package cl.ucn.app.model.biblioteca;

import jakarta.persistence.*;

@Entity
@Table(name = "ejemplar")
public class Ejemplar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "estado", nullable = false, length = 20)
    private String estado = "DISPONIBLE";

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "libro_id", nullable = false)
    private Libro libro;

    public Ejemplar() {}

    // Getters y Setters
    public Long getId() { return id; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Libro getLibro() { return libro; }
    public void setLibro(Libro libro) { this.libro = libro; }
}