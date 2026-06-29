package cl.ucn.app.service.biblioteca;

import cl.ucn.app.model.biblioteca.Ejemplar;
import cl.ucn.app.model.biblioteca.EstadoEjemplar;
import cl.ucn.app.model.biblioteca.EstadoPrestamo;
import cl.ucn.app.model.biblioteca.Lector;
import cl.ucn.app.model.biblioteca.Libro;
import cl.ucn.app.model.biblioteca.Multa;
import cl.ucn.app.model.biblioteca.PrestamoLibro;
import cl.ucn.app.repository.biblioteca.EjemplarRepository;
import cl.ucn.app.repository.biblioteca.LibroRepository;
import cl.ucn.app.repository.biblioteca.MultaRepository;
import cl.ucn.app.repository.biblioteca.PrestamoLibroRepository;
import cl.ucn.app.repository.biblioteca.api.IEjemplarRepository;
import cl.ucn.app.repository.biblioteca.api.ILibroRepository;
import cl.ucn.app.repository.biblioteca.api.IMultaRepository;
import cl.ucn.app.repository.biblioteca.api.IPrestamoLibroRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardService {

    private final ILibroRepository libroRepository;
    private final IEjemplarRepository ejemplarRepository;
    private final IPrestamoLibroRepository prestamoRepository;
    private final IMultaRepository multaRepository;

    public DashboardService() {
        this.libroRepository = new LibroRepository();
        this.ejemplarRepository = new EjemplarRepository();
        this.prestamoRepository = new PrestamoLibroRepository();
        this.multaRepository = new MultaRepository();
    }

    public DashboardService(ILibroRepository libroRepository,
                            IEjemplarRepository ejemplarRepository,
                            IPrestamoLibroRepository prestamoRepository,
                            IMultaRepository multaRepository) {
        this.libroRepository = libroRepository;
        this.ejemplarRepository = ejemplarRepository;
        this.prestamoRepository = prestamoRepository;
        this.multaRepository = multaRepository;
    }

    public Kpis calcularKpis() {
        List<Libro> libros = libroRepository.findAll();
        int totalLibros = libros.size();
        int totalEjemplares = 0;
        for (Libro l : libros) {
            totalEjemplares += ejemplarRepository.findByLibro(l).size();
        }
        int prestamosActivos = 0;
        int prestamosAtrasados = 0;
        for (PrestamoLibro p : prestamoRepository.findAll()) {
            if (p.getEstado() == EstadoPrestamo.ACTIVO) {
                prestamosActivos++;
                if (p.getFechaVencimiento() != null && p.getFechaVencimiento().isBefore(LocalDate.now())) {
                    prestamosAtrasados++;
                }
            }
        }
        BigDecimal totalMultasAcumuladas = BigDecimal.ZERO;
        for (Multa m : multaRepository.findAll()) {
            if (!m.getPagada() && m.getMonto() != null) {
                totalMultasAcumuladas = totalMultasAcumuladas.add(m.getMonto());
            }
        }
        return new Kpis(totalLibros, totalEjemplares, prestamosActivos, prestamosAtrasados, totalMultasAcumuladas);
    }

    public Map<Long, Integer> totalesPorLibro(List<Libro> libros) {
        Map<Long, Integer> totales = new HashMap<>();
        for (Libro libro : libros) {
            totales.put(libro.getId(), ejemplarRepository.findByLibro(libro).size());
        }
        return totales;
    }

    public Map<Long, Integer> disponiblesPorLibro(List<Libro> libros) {
        Map<Long, Integer> disponibles = new HashMap<>();
        for (Libro libro : libros) {
            int disp = 0;
            for (Ejemplar e : ejemplarRepository.findByLibro(libro)) {
                if (e.getEstado() == EstadoEjemplar.DISPONIBLE) {
                    disp++;
                }
            }
            disponibles.put(libro.getId(), disp);
        }
        return disponibles;
    }

    public BigDecimal multasPendientesDelLector(Lector lector) {
        if (lector == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal total = BigDecimal.ZERO;
        for (Multa m : multaRepository.findPendientesByLector(lector)) {
            if (m.getMonto() != null) {
                total = total.add(m.getMonto());
            }
        }
        return total;
    }

    public List<PrestamoLibro> entregasPendientes() {
        List<PrestamoLibro> pendientes = new ArrayList<>();
        for (PrestamoLibro p : prestamoRepository.findAll()) {
            if (p.getEstado() == EstadoPrestamo.SOLICITADO) {
                pendientes.add(p);
            }
        }
        return pendientes;
    }

    public List<PrestamoLibro> devolucionesPendientes() {
        List<PrestamoLibro> pendientes = new ArrayList<>();
        for (PrestamoLibro p : prestamoRepository.findAll()) {
            if (p.getEstado() == EstadoPrestamo.PENDIENTE_DEVOLUCION) {
                pendientes.add(p);
            }
        }
        return pendientes;
    }

    public record Kpis(int totalLibros, int totalEjemplares, int prestamosActivos,
                       int prestamosAtrasados, BigDecimal totalMultas) {
    }
}
