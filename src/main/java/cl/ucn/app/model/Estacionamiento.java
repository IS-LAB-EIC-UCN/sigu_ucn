package cl.ucn.app.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "estacionamientos")
public class Estacionamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String codigo;

    @Column(nullable = false)
    private Boolean habilitado = true;

    public Estacionamiento() {
    }

    public Estacionamiento(String codigo, Boolean habilitado) {
        this.codigo = codigo;
        this.habilitado = habilitado;
    }

    public Long getId() {
        return id;
    }

    public String getCodigo() {
        return codigo;
    }

    public Boolean getHabilitado() {
        return habilitado;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public void setHabilitado(Boolean habilitado) {
        this.habilitado = habilitado;
    }
}
