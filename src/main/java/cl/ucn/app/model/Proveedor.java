package cl.ucn.app.model;

import jakarta.persistence.*;

/**
 * Proveedor de equipos e insumos del laboratorio (Grupo 5 - Inventario).
 * El "identificador" corresponde al RUT u otro identificador único del proveedor.
 */
@Entity
@Table(name = "inv_proveedores")
public class Proveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String identificador;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(length = 30)
    private String telefono;

    @Column(length = 120)
    private String correo;

    @Column(length = 200)
    private String direccion;

    public Proveedor() {
    }

    public Proveedor(String identificador, String nombre, String telefono, String correo, String direccion) {
        this.identificador = identificador;
        this.nombre = nombre;
        this.telefono = telefono;
        this.correo = correo;
        this.direccion = direccion;
    }

    public Long getId() {
        return id;
    }

    public String getIdentificador() {
        return identificador;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
}
