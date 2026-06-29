package cl.ucn.app.controller.biblioteca;

import cl.ucn.app.model.biblioteca.Ejemplar;
import cl.ucn.app.model.biblioteca.Lector;
import cl.ucn.app.model.biblioteca.Libro;
import cl.ucn.app.model.biblioteca.Multa;
import cl.ucn.app.model.biblioteca.PrestamoLibro;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PrestamoConMulta {
    private final PrestamoLibro prestamo;
    private final Multa multa;

    public PrestamoConMulta(PrestamoLibro prestamo, Multa multa) {
        this.prestamo = prestamo;
        this.multa = multa;
    }

    public PrestamoLibro getPrestamo() { return prestamo; }
    public Multa getMulta() { return multa; }

    public Long getId() {
        return prestamo != null ? prestamo.getId() : null;
    }

    public Lector getLector() {
        return prestamo != null ? prestamo.getLector() : null;
    }

    public String getLectorNombre() {
        Lector l = getLector();
        return l != null ? l.getNombre() : "N/A";
    }

    public String getLectorRut() {
        Lector l = getLector();
        return l != null ? l.getRut() : "N/A";
    }

    public String getLibroTitulo() {
        if (prestamo == null) {
            return "N/A";
        }
        Ejemplar ej = prestamo.getEjemplar();
        if (ej == null) {
            return "N/A";
        }
        Libro libro = ej.getLibro();
        return libro != null ? libro.getTitulo() : "N/A";
    }

    public LocalDate getFechaInicio() {
        return prestamo != null ? prestamo.getFechaInicio() : null;
    }

    public LocalDate getFechaVencimiento() {
        return prestamo != null ? prestamo.getFechaVencimiento() : null;
    }

    public LocalDate getFechaDevolucion() {
        return prestamo != null ? prestamo.getFechaDevolucion() : null;
    }

    public String getEstado() {
        return prestamo != null && prestamo.getEstado() != null ? prestamo.getEstado().name() : "N/A";
    }

    public BigDecimal getMontoMulta() {
        return multa != null ? multa.getMonto() : null;
    }

    public Integer getDiasAtraso() {
        return multa != null ? multa.getDiasAtraso() : null;
    }
}
