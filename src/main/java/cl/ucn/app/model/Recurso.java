package cl.ucn.app.model;

@Entity
@Table(name = "recursos")
public class Recurso {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @column(nullable = false, length = 100)
    private String nombre;

    @column(name = "stock", nullable = false)
    private int stock;

    @column(nullable = false, length = 120)
    private String tipo;

    public Recurso() {}
    public Recurso(String nombre, int stock, String tipo) {
        this.nombre = nombre;
        this.stock = stock;
        this.tipo = tipo;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public int getStock() {
        return stock;
    }

    public String getTipo() {
        return tipo;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public void getTipo(String tipo) {
        this.tipo = tipo;
    }
    @Override
    public String toString() {
        return "Recurso [id=" + id + ", nombre=" + nombre + ", stock=" + stock + ", tipo=" + tipo + "]";
    }
    
}
