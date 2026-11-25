
package cl.ucn.app.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoEstacionamiento tipo = TipoEstacionamiento.COMUN;

    public Estacionamiento() {
    }

    // Constructor que conserva la compatibilidad con los tests existentes.
    public Estacionamiento(String codigo, Boolean habilitado) {
        this(codigo, habilitado, TipoEstacionamiento.COMUN);
    }

    public Estacionamiento(
            String codigo,
            Boolean habilitado,
            TipoEstacionamiento tipo) {
        this.codigo = codigo;
        this.habilitado = habilitado;
        this.tipo = tipo;
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

    public TipoEstacionamiento getTipo() {
        return tipo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public void setHabilitado(Boolean habilitado) {
        this.habilitado = habilitado;
    }

    public void setTipo(TipoEstacionamiento tipo) {
        this.tipo = tipo;
    }
}
