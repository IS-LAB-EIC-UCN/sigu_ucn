package cl.ucn.app.model;

import cl.ucn.app.exception.ReglaNegocioException;
import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Préstamo de un equipo de laboratorio a un usuario solicitante (Grupo 5 - Inventario).
 * <p>
 * Reglas de negocio:
 * <ul>
 *     <li>Un equipo prestado no puede prestarse nuevamente (lo valida {@link Equipo#marcarPrestado()}).</li>
 *     <li>Una devolución solo puede registrarse una vez por préstamo.</li>
 * </ul>
 * Las fechas se guardan en UTC y se convierten a America/Santiago solo al mostrarlas.
 */
@Entity
@Table(name = "inv_prestamos")
public class Prestamo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "equipo_id", nullable = false)
    private Equipo equipo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    /** Quien recibe el equipo al devolverlo; nulo mientras el préstamo está ACTIVO. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "devuelto_por_id")
    private Usuario devueltoPor;

    @Column(name = "fecha_prestamo", nullable = false)
    private LocalDateTime fechaPrestamo;

    @Column(name = "fecha_devolucion")
    private LocalDateTime fechaDevolucion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoPrestamo estado = EstadoPrestamo.ACTIVO;

    public Prestamo() {
    }

    /**
     * CU03: crea un préstamo ACTIVO y deja el equipo en estado PRESTADO.
     *
     * @throws cl.ucn.app.exception.EquipoNoDisponibleException si el equipo ya está prestado o dado de baja
     */
    public static Prestamo iniciar(Equipo equipo, Usuario solicitante, LocalDateTime ahora) {
        if (equipo == null || solicitante == null) {
            throw new ReglaNegocioException("El préstamo requiere un equipo y un solicitante.");
        }
        equipo.marcarPrestado();

        Prestamo prestamo = new Prestamo();
        prestamo.equipo = equipo;
        prestamo.usuario = solicitante;
        prestamo.fechaPrestamo = ahora;
        prestamo.estado = EstadoPrestamo.ACTIVO;
        return prestamo;
    }

    /**
     * CU04: finaliza el préstamo, registra quién recibió el equipo y lo deja DISPONIBLE.
     */
    public void finalizar(Usuario receptor, LocalDateTime ahora) {
        if (estado == EstadoPrestamo.FINALIZADO) {
            throw new ReglaNegocioException("La devolución de este préstamo ya fue registrada.");
        }
        if (receptor == null) {
            throw new ReglaNegocioException("Debe indicarse quién recibe el equipo.");
        }
        if (ahora.isBefore(fechaPrestamo)) {
            throw new ReglaNegocioException("La fecha de devolución no puede ser anterior a la del préstamo.");
        }
        this.fechaDevolucion = ahora;
        this.devueltoPor = receptor;
        this.estado = EstadoPrestamo.FINALIZADO;
        this.equipo.marcarDisponible();
    }

    public boolean estaActivo() {
        return estado == EstadoPrestamo.ACTIVO;
    }

    public Long getId() {
        return id;
    }

    public Equipo getEquipo() {
        return equipo;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public Usuario getDevueltoPor() {
        return devueltoPor;
    }

    public LocalDateTime getFechaPrestamo() {
        return fechaPrestamo;
    }

    public LocalDateTime getFechaDevolucion() {
        return fechaDevolucion;
    }

    public EstadoPrestamo getEstado() {
        return estado;
    }
}
