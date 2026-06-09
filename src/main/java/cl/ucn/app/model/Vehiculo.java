package cl.ucn.app.model;

import jakarta.persistence.*;

@Entity
@Table(name = "vehiculos")
public class Vehiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 10)
    private String patente;

    @Column(nullable = false, length = 50)
    private String marca;

    @Column(nullable = false, length = 50)
    private String modelo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    public Vehiculo() {
    }

    public Vehiculo(String patente, String marca, String modelo, Usuario usuario) {
        this.patente = patente;
        this.marca = marca;
        this.modelo = modelo;
        this.usuario = usuario;
    }

    public Long getId() {
        return id;
    }

    public String getPatente() {
        return patente;
    }

    public String getMarca() {
        return marca;
    }

    public String getModelo() {
        return modelo;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setPatente(String patente) {
        this.patente = patente;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}