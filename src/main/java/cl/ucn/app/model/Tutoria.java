package cl.ucn.app.model;

import jakarta.persistence.*;

import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Table(name = "tut_tutorias")
public class Tutoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tutor_id", nullable = false)
    private Tutor tutor;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "asignatura_id", nullable = false)
    private Asignatura asignatura;

    @Column(nullable = false)
    private LocalDateTime inicio;

    @Column(nullable = false)
    private LocalDateTime fin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ModalidadTutoria modalidad;

    // Solo obligatoria en modalidad PRESENCIAL.
    @Column(length = 50)
    private String sala;

    @Column(nullable = false)
    private Integer capacidad;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoTutoria estado = EstadoTutoria.PUBLICADA;

    @Column(name = "motivo_cancelacion", length = 255)
    private String motivoCancelacion;

    public Tutoria() {
    }

    public Tutoria(Tutor tutor, Asignatura asignatura, LocalDateTime inicio, LocalDateTime fin,
                   ModalidadTutoria modalidad, String sala, Integer capacidad) {
        this.tutor = tutor;
        this.asignatura = asignatura;
        this.inicio = inicio;
        this.fin = fin;
        this.modalidad = modalidad;
        this.sala = sala;
        this.capacidad = capacidad;
        this.estado = EstadoTutoria.PUBLICADA;
    }

    public long getDuracionMinutos() {
        return Duration.between(inicio, fin).toMinutes();
    }

    public boolean estaDisponible() {
        return estado == EstadoTutoria.PUBLICADA && inicio.isAfter(LocalDateTime.now());
    }

    public Long getId() {
        return id;
    }

    public Tutor getTutor() {
        return tutor;
    }

    public void setTutor(Tutor tutor) {
        this.tutor = tutor;
    }

    public Asignatura getAsignatura() {
        return asignatura;
    }

    public void setAsignatura(Asignatura asignatura) {
        this.asignatura = asignatura;
    }

    public LocalDateTime getInicio() {
        return inicio;
    }

    public void setInicio(LocalDateTime inicio) {
        this.inicio = inicio;
    }

    public LocalDateTime getFin() {
        return fin;
    }

    public void setFin(LocalDateTime fin) {
        this.fin = fin;
    }

    public ModalidadTutoria getModalidad() {
        return modalidad;
    }

    public void setModalidad(ModalidadTutoria modalidad) {
        this.modalidad = modalidad;
    }

    public String getSala() {
        return sala;
    }

    public void setSala(String sala) {
        this.sala = sala;
    }

    public Integer getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(Integer capacidad) {
        this.capacidad = capacidad;
    }

    public EstadoTutoria getEstado() {
        return estado;
    }

    public void setEstado(EstadoTutoria estado) {
        this.estado = estado;
    }

    public String getMotivoCancelacion() {
        return motivoCancelacion;
    }

    public void setMotivoCancelacion(String motivoCancelacion) {
        this.motivoCancelacion = motivoCancelacion;
    }
}