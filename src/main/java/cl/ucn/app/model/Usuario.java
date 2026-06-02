package cl.ucn.app.model;

import cl.ucn.app.service.Interfaces.IEstrategiaFiltrado;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, unique = true, length = 120)
    private String correo;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false)
    private Boolean activo = true;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "rol_id", nullable = false)
    private Rol rol;

    private IEstrategiaFiltrado estrategia_filtrado;

    public Usuario() {}

    public Usuario(String nombre, String correo, String password, Boolean activo, Rol rol) {
        this.nombre = nombre;
        this.correo = correo;
        this.password = password;
        this.activo = activo;
        this.rol = rol;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public String getPassword() {
        return password;
    }

    public Boolean getActivo() {
        return activo;
    }

    public Rol getRol() {
        return rol;
    }

    public IEstrategiaFiltrado getEstrategia_filtrado() {
        return estrategia_filtrado;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public void setEstrategia_filtrado(IEstrategiaFiltrado estrategia_filtrado) {
        this.estrategia_filtrado = estrategia_filtrado;
    }
}