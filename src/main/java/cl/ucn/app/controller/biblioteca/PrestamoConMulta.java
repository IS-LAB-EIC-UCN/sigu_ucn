package cl.ucn.app.controller.biblioteca;

import cl.ucn.app.model.biblioteca.Lector;
import cl.ucn.app.model.biblioteca.Multa;
import cl.ucn.app.model.biblioteca.PrestamoLibro;

public class PrestamoConMulta {
    private final PrestamoLibro prestamo;
    private final Multa multa;

    public PrestamoConMulta(PrestamoLibro prestamo, Multa multa) {
        this.prestamo = prestamo;
        this.multa = multa;
    }

    public PrestamoLibro getPrestamo() { return prestamo; }
    public Multa getMulta() { return multa; }

    public Lector getLector() { return prestamo.getLector(); }
    public Long getId() { return prestamo.getId(); }
    public String getLibroTitulo() { return prestamo.getEjemplar() != null && prestamo.getEjemplar().getLibro() != null ? prestamo.getEjemplar().getLibro().getTitulo() : "N/A"; }
    public java.time.LocalDate getFechaInicio() { return prestamo.getFechaInicio(); }
    public java.time.LocalDate getFechaVencimiento() { return prestamo.getFechaVencimiento(); }
    public java.time.LocalDate getFechaDevolucion() { return prestamo.getFechaDevolucion(); }
    public String getEstado() { return prestamo.getEstado() != null ? prestamo.getEstado().name() : "N/A"; }
    public java.math.BigDecimal getMontoMulta() {
        return multa != null ? multa.getMonto() : null;
    }
    public Integer getDiasAtraso() {
        return multa != null ? multa.getDiasAtraso() : null;
    }
}
