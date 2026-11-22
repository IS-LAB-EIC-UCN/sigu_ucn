package cl.ucn.app.model.soporte;

import cl.ucn.app.model.Usuario;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "tickets")
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoTicket estado;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tecnico_id")
    private Usuario tecnicoAsignado;

    @Column(columnDefinition = "TEXT")
    private String resolucion;

    @Column(name = "fecha_cierre")
    private LocalDateTime fechaCierre;

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.PERSIST)
    @OrderBy("fechaCambio ASC, id ASC")
    private List<HistorialTicket> historial = new ArrayList<>();

    public Ticket() {}

    public Ticket(String titulo, String descripcion, Usuario usuario) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.usuario = usuario;
        this.estado = EstadoTicket.CREADO;
        this.fechaCreacion = LocalDateTime.now();
        registrar(usuario, "Ticket creado", null, EstadoTicket.CREADO);
    }

    public void asignarTecnico(Usuario tecnico, Usuario administrador) {
        Objects.requireNonNull(tecnico, "El técnico es obligatorio.");
        Objects.requireNonNull(administrador, "El administrador es obligatorio.");

        if (estaCerrado()) {
            throw new IllegalStateException("No se puede modificar un ticket cerrado.");
        }

        if (estado != EstadoTicket.CREADO && estado != EstadoTicket.ASIGNADO) {
            throw new IllegalStateException("El ticket no está disponible para asignación.");
        }

        EstadoTicket anterior = estado;
        this.tecnicoAsignado = tecnico;
        this.estado = EstadoTicket.ASIGNADO;
        registrar(administrador, "Técnico asignado: " + tecnico.getNombre(), anterior, estado);
    }

    public void iniciarResolucion(Usuario tecnico) {
        Objects.requireNonNull(tecnico, "El técnico es obligatorio.");

        if (!mismoUsuario(tecnicoAsignado, tecnico)) {
            throw new SecurityException("Solo el técnico asignado puede iniciar la resolución.");
        }

        if (estado != EstadoTicket.ASIGNADO) {
            throw new IllegalStateException("El ticket no está asignado o ya inició su resolución.");
        }

        this.estado = EstadoTicket.EN_RESOLUCION;
        registrar(tecnico, "Resolución iniciada", EstadoTicket.ASIGNADO, estado);
    }

    public void registrarResolucion(String texto, Usuario tecnico) {
        validarCierrePorTecnico(tecnico);

        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException("La resolución es obligatoria.");
        }

        this.resolucion = texto.trim();
        registrar(tecnico, "Resolución registrada", null, null);
    }

    public void cerrar(Usuario tecnico) {
        validarCierrePorTecnico(tecnico);

        if (resolucion == null || resolucion.isBlank()) {
            throw new IllegalStateException("No se puede cerrar un ticket sin resolución registrada.");
        }

        this.estado = EstadoTicket.CERRADO;
        this.fechaCierre = LocalDateTime.now();
        registrar(tecnico, "Ticket cerrado", EstadoTicket.EN_RESOLUCION, estado);
    }

    public boolean estaCerrado() {
        return estado == EstadoTicket.CERRADO;
    }

    private void validarCierrePorTecnico(Usuario tecnico) {
        Objects.requireNonNull(tecnico, "El técnico es obligatorio.");

        if (estaCerrado()) {
            throw new IllegalStateException("No se puede modificar un ticket cerrado.");
        }

        if (!mismoUsuario(tecnicoAsignado, tecnico)) {
            throw new SecurityException("Solo el técnico asignado puede cerrar el ticket.");
        }

        if (estado != EstadoTicket.EN_RESOLUCION) {
            throw new IllegalStateException("El ticket debe estar en resolución para cerrarse.");
        }
    }

    private void registrar(Usuario autor, String accion, EstadoTicket anterior, EstadoTicket nuevo) {
        historial.add(new HistorialTicket(this, autor, accion, anterior, nuevo));
    }

    private static boolean mismoUsuario(Usuario a, Usuario b) {
        if (a == null || b == null) {return false;}
        return a == b || (a.getId() != null && a.getId().equals(b.getId()));
    }

    public Long getId() {return id;}
    public String getTitulo() {return titulo;}
    public String getDescripcion() {return descripcion;}
    public EstadoTicket getEstado() {return estado;}
    public LocalDateTime getFechaCreacion() {return fechaCreacion;}
    public Usuario getUsuario() {return usuario;}
    public Usuario getTecnicoAsignado() {return tecnicoAsignado;}
    public String getResolucion() {return resolucion;}
    public LocalDateTime getFechaCierre() {return fechaCierre;}
    public List<HistorialTicket> getHistorial() {return Collections.unmodifiableList(historial);}
}
