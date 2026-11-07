package cl.ucn.app.model;

import jakarta.persistence.*;

@Entity
@Table(name = "ejemplares")
public class Ejemplar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo_inventario", nullable = false, unique = true, length = 50)
    private String codigoInventario;

    @Column(nullable = false, length = 100)
    private String ubicacion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoEjemplar estado = EstadoEjemplar.DISPONIBLE;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "libro_id", nullable = false)
    private Libro libro;

    public Ejemplar() {
    }

    public Ejemplar(String codigoInventario, String ubicacion, Libro libro) {
        this.codigoInventario = codigoInventario;
        this.ubicacion = ubicacion;
        this.libro = libro;
        this.estado = EstadoEjemplar.DISPONIBLE;
    }

    public Long getId() {
        return id;
    }

    public String getCodigoInventario() {
        return codigoInventario;
    }

    public void setCodigoInventario(String codigoInventario) {
        this.codigoInventario = codigoInventario;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public EstadoEjemplar getEstado() {
        return estado;
    }

    public void setEstado(EstadoEjemplar estado) {
        this.estado = estado;
    }

    public Libro getLibro() {
        return libro;
    }

    public void setLibro(Libro libro) {
        this.libro = libro;
    }

    public boolean estaDisponible() {
        return this.estado == EstadoEjemplar.DISPONIBLE;
    }

    public void marcarPrestado() {
        this.estado = EstadoEjemplar.PRESTADO;
    }

    public void marcarDisponible() {
        this.estado = EstadoEjemplar.DISPONIBLE;
    }

    public void marcarEnReparacion() {
        this.estado = EstadoEjemplar.EN_REPARACION;
    }

    public void darDeBaja() {
        this.estado = EstadoEjemplar.DADO_DE_BAJA;
    }
}
