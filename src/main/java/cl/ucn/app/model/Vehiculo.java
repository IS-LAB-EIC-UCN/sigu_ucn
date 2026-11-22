package cl.ucn.app.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "vehiculos")
public class Vehiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 10)
    private String patente;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario propietario;

    protected Vehiculo() {
    }

    public Vehiculo(String patente, Usuario propietario) {
        this.patente = patente;
        this.propietario = propietario;
    }

    public Long getId() {
        return id;
    }

    public String getPatente() {
        return patente;
    }

    public Usuario getPropietario() {
        return propietario;
    }
}