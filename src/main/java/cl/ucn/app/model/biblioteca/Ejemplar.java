package cl.ucn.app.model.biblioteca;

import jakarta.persistence.*;

@Entity
@Table(name = "ejemplar")
public class Ejemplar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoEjemplar estado = EstadoEjemplar.DISPONIBLE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "libro_id", nullable = false)
    private Libro libro;

    public Ejemplar() {}

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public EstadoEjemplar getEstado() { return estado; }
    public void setEstado(EstadoEjemplar estado) { this.estado = estado; }

    public Libro getLibro() { return libro; }
    public void setLibro(Libro libro) { this.libro = libro; }
}