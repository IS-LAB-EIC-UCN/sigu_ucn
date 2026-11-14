package cl.ucn.app.model;

import jakarta.persistence.*;

/**
 * Recurso genérico del inventario de laboratorio (Grupo 5 - Inventario).
 */
@Entity
@Table(name = "inv_productos")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo", discriminatorType = DiscriminatorType.STRING, length = 10)
public abstract class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String codigo;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 255)
    private String descripcion;

    @Column(nullable = false)
    private Boolean activo = true;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proveedor_id")
    private Proveedor proveedor;

    protected Producto() {
    }

    protected Producto(String codigo, String nombre, String descripcion, Categoria categoria, Proveedor proveedor) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.proveedor = proveedor;
        this.activo = true;
    }

    /**
     * Tipo de recurso para mostrar en la interfaz ("EQUIPO" o "INSUMO").
     */
    public abstract String getTipoRecurso();

    /**
     * Texto de disponibilidad para la consulta de inventario.
     * Un equipo informa su estado y un insumo su stock.
     */
    public abstract String getDisponibilidad();

    public Long getId() {
        return id;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public Boolean getActivo() {
        return activo;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public Proveedor getProveedor() {
        return proveedor;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }
}
