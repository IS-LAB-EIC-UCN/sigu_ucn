package cl.ucn.app.model;

import jakarta.persistence.*;

@Entity
@Table(name = "lectores")
public class Lector {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 12)
    private String rut;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, unique = true, length = 120)
    private String correo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoLector tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoLector estado = EstadoLector.HABILITADO;

    public Lector() {
    }

    public Lector(String rut, String nombre, String correo, TipoLector tipo) {
        this.rut = rut;
        this.nombre = nombre;
        this.correo = correo;
        this.tipo = tipo;
        this.estado = EstadoLector.HABILITADO;
    }

    public Long getId() {
        return id;
    }

    public String getRut() {
        return rut;
    }

    public void setRut(String rut) {
        this.rut = rut;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public TipoLector getTipo() {
        return tipo;
    }

    public void setTipo(TipoLector tipo) {
        this.tipo = tipo;
    }

    public EstadoLector getEstado() {
        return estado;
    }

    public void setEstado(EstadoLector estado) {
        this.estado = estado;
    }

    public boolean estaHabilitado() {
        return this.estado == EstadoLector.HABILITADO;
    }

    public void bloquear() {
        this.estado = EstadoLector.BLOQUEADO;
    }

    public void habilitar() {
        this.estado = EstadoLector.HABILITADO;
    }
}
