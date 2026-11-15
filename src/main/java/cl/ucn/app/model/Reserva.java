package cl.ucn.app.model;

import cl.ucn.app.exception.TransicionEstadoInvalidaException;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Reserva de una sala (tabla reservas).
 * El estado solo cambia mediante aprobar(), rechazar() y cancelar(),
 * que validan las transiciones permitidas (RN-03, RF-06, RF-07).
 */
@Entity
@Table(name = "reservas")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fecha_reserva", nullable = false)
    private LocalDate fechaReserva;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoReserva estado = EstadoReserva.PENDIENTE;

    @Column(length = 255)
    private String motivo;

    @Column(name = "motivo_rechazo", length = 255)
    private String motivoRechazo;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "espacio_id", nullable = false)
    private Espacio espacio;

    public Reserva() {
    }

    /**
     * RF-05: toda reserva nueva nace en estado PENDIENTE y asociada a quien la creó.
     */
    public Reserva(LocalDate fechaReserva, LocalTime horaInicio, LocalTime horaFin,
                   String motivo, Usuario usuario, Espacio espacio) {
        this.fechaReserva = fechaReserva;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.motivo = motivo;
        this.usuario = usuario;
        this.espacio = espacio;
        this.estado = EstadoReserva.PENDIENTE;
    }

    @PrePersist
    void alCrear() {
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
        if (estado == null) {
            estado = EstadoReserva.PENDIENTE;
        }
    }

    // ------------------------------------------------------------------
    // Transiciones de estado
    // ------------------------------------------------------------------

    /** RF-07 / RN-03: solo una reserva PENDIENTE puede aprobarse. */
    public void aprobar() {
        if (estado != EstadoReserva.PENDIENTE) {
            throw new TransicionEstadoInvalidaException(estado, EstadoReserva.APROBADA,
                    "Solo se pueden aprobar reservas pendientes. Estado actual: " + estado);
        }
        estado = EstadoReserva.APROBADA;
    }

    /** RF-07: solo una reserva PENDIENTE puede rechazarse, y el rechazo exige un motivo. */
    public void rechazar(String motivoRechazo) {
        if (estado != EstadoReserva.PENDIENTE) {
            throw new TransicionEstadoInvalidaException(estado, EstadoReserva.RECHAZADA,
                    "Solo se pueden rechazar reservas pendientes. Estado actual: " + estado);
        }
        if (motivoRechazo == null || motivoRechazo.isBlank()) {
            throw new IllegalArgumentException("Debe indicar el motivo del rechazo.");
        }
        this.motivoRechazo = motivoRechazo.trim();
        estado = EstadoReserva.RECHAZADA;
    }

    /**
     * RF-06: se puede cancelar una reserva PENDIENTE o APROBADA que aún no ha comenzado.
     * Que el usuario sea el dueño de la reserva se valida en la capa de servicio.
     */
    public void cancelar(LocalDateTime ahora) {
        if (!estado.esActiva()) {
            throw new TransicionEstadoInvalidaException(estado, EstadoReserva.CANCELADA,
                    "Solo se pueden cancelar reservas pendientes o aprobadas. Estado actual: " + estado);
        }
        if (haComenzado(ahora)) {
            throw new TransicionEstadoInvalidaException(estado, EstadoReserva.CANCELADA,
                    "No se puede cancelar una reserva que ya comenzó.");
        }
        estado = EstadoReserva.CANCELADA;
    }

    // ------------------------------------------------------------------
    // Consultas de dominio
    // ------------------------------------------------------------------

    /** RN-05: solo las reservas activas ocupan el bloque horario. */
    public boolean estaActiva() {
        return estado.esActiva();
    }

    public LocalDateTime getInicio() {
        return LocalDateTime.of(fechaReserva, horaInicio);
    }

    public LocalDateTime getFin() {
        return LocalDateTime.of(fechaReserva, horaFin);
    }

    public boolean haComenzado(LocalDateTime ahora) {
        return !ahora.isBefore(getInicio());
    }

    /**
     * RN-01: dos reservas de la misma sala se solapan si sus intervalos [inicio, fin)
     * se intersectan. Mismo criterio que la restricción excl_reservas_sin_solape.
     */
    public boolean seSolapaCon(LocalDate fecha, LocalTime inicio, LocalTime fin) {
        return fechaReserva.equals(fecha)
                && horaInicio.isBefore(fin)
                && inicio.isBefore(horaFin);
    }

    // ------------------------------------------------------------------
    // Getters / setters (sin setEstado: el estado cambia solo por transiciones)
    // ------------------------------------------------------------------

    public Long getId() {
        return id;
    }

    public LocalDate getFechaReserva() {
        return fechaReserva;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public EstadoReserva getEstado() {
        return estado;
    }

    public String getMotivo() {
        return motivo;
    }

    public String getMotivoRechazo() {
        return motivoRechazo;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public Espacio getEspacio() {
        return espacio;
    }

    public void setFechaReserva(LocalDate fechaReserva) {
        this.fechaReserva = fechaReserva;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public void setHoraFin(LocalTime horaFin) {
        this.horaFin = horaFin;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public void setEspacio(Espacio espacio) {
        this.espacio = espacio;
    }
}
