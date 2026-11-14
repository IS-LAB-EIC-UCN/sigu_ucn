package cl.ucn.app.model;

import cl.ucn.app.exception.ReglaNegocioException;
import cl.ucn.app.exception.StockInsuficienteException;
import jakarta.persistence.*;

/**
 * Insumo consumible del laboratorio, controlado por stock (Grupo 5 - Inventario).
 * <p>
 * Reglas de negocio:
 * <ul>
 *     <li>El stock no puede quedar negativo.</li>
 *     <li>Ciertos insumos deben mantener un stock mínimo (alerta de stock bajo).</li>
 *     <li>El stock inicial solo puede registrarse una vez (CU02 - A4).</li>
 * </ul>
 */
@Entity
@DiscriminatorValue("INSUMO")
public class Insumo extends Producto {

    @Column(name = "stock_actual")
    private Integer stockActual = 0;

    @Column(name = "stock_minimo")
    private Integer stockMinimo = 0;

    @Column(name = "stock_inicializado")
    private Boolean stockInicializado = false;

    public Insumo() {
    }

    public Insumo(String codigo, String nombre, String descripcion, Categoria categoria,
                  Proveedor proveedor, int stockMinimo) {
        super(codigo, nombre, descripcion, categoria, proveedor);
        validarStockMinimo(stockMinimo);
        this.stockActual = 0;
        this.stockMinimo = stockMinimo;
        this.stockInicializado = false;
    }

    public void registrarStockInicial(int cantidad) {
        if (Boolean.TRUE.equals(stockInicializado)) {
            throw new ReglaNegocioException("El stock inicial de " + getCodigo()
                    + " ya fue registrado. Use una entrada o salida de inventario.");
        }
        if (cantidad < 0) {
            throw new ReglaNegocioException("El stock inicial no puede ser negativo.");
        }
        this.stockActual = cantidad;
        this.stockInicializado = true;
    }

    public void registrarEntrada(int cantidad) {
        validarCantidadPositiva(cantidad);
        this.stockActual = stockActual + cantidad;
        this.stockInicializado = true;
    }

    public void registrarSalida(int cantidad) {
        validarCantidadPositiva(cantidad);
        if (cantidad > stockActual) {
            throw new StockInsuficienteException(getCodigo(), stockActual, cantidad);
        }
        this.stockActual = stockActual - cantidad;
    }

    /**
     * Stock bajo cuando el stock actual es menor o igual al mínimo (CU02, paso 12).
     */
    public boolean tieneStockBajo() {
        return stockActual <= stockMinimo;
    }

    private void validarCantidadPositiva(int cantidad) {
        if (cantidad <= 0) {
            throw new ReglaNegocioException("La cantidad debe ser mayor a cero.");
        }
    }

    private static void validarStockMinimo(Integer stockMinimo) {
        if (stockMinimo == null || stockMinimo < 0) {
            throw new ReglaNegocioException("El stock mínimo no puede ser negativo.");
        }
    }

    @Override
    public String getTipoRecurso() {
        return "INSUMO";
    }

    @Override
    public String getDisponibilidad() {
        return "Stock: " + stockActual + " (mín. " + stockMinimo + ")";
    }

    public Integer getStockActual() {
        return stockActual;
    }

    public Integer getStockMinimo() {
        return stockMinimo;
    }

    public Boolean getStockInicializado() {
        return stockInicializado;
    }

    public void setStockMinimo(Integer stockMinimo) {
        validarStockMinimo(stockMinimo);
        this.stockMinimo = stockMinimo;
    }
}
