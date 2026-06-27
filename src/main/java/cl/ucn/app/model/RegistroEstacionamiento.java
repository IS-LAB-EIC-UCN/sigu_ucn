package cl.ucn.app.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "registros_estacionamiento")
public class RegistroEstacionamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "reserva_id", nullable = false)
    private Reserva reserva;

    @Column(name = "fecha_hora_ingreso", nullable = false)
    private LocalDateTime fechaHoraIngreso;

    @Column(name = "fecha_hora_salida")
    private LocalDateTime fechaHoraSalida;

    @Column(name = "tiempo_uso_minutos")
    private Integer tiempoUsoMinutos;

    @Column(name = "estado", nullable = false, length = 30)
    private String estado;

    public RegistroEstacionamiento() {
    }

    public RegistroEstacionamiento(Reserva reserva, LocalDateTime fechaHoraIngreso, String estado) {
        this.reserva = reserva;
        this.fechaHoraIngreso = fechaHoraIngreso;
        this.estado = estado;
    }

    public Long getId() {
        return id;
    }

    public Reserva getReserva() {
        return reserva;
    }

    public void setReserva(Reserva reserva) {
        this.reserva = reserva;
    }

    public LocalDateTime getFechaHoraIngreso() {
        return fechaHoraIngreso;
    }

    public void setFechaHoraIngreso(LocalDateTime fechaHoraIngreso) {
        this.fechaHoraIngreso = fechaHoraIngreso;
    }

    public LocalDateTime getFechaHoraSalida() {
        return fechaHoraSalida;
    }

    public void setFechaHoraSalida(LocalDateTime fechaHoraSalida) {
        this.fechaHoraSalida = fechaHoraSalida;
    }

    public Integer getTiempoUsoMinutos() {
        return tiempoUsoMinutos;
    }

    public void setTiempoUsoMinutos(Integer tiempoUsoMinutos) {
        this.tiempoUsoMinutos = tiempoUsoMinutos;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    
}