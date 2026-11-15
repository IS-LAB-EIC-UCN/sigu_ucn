package cl.ucn.app.service.talleres;

import cl.ucn.app.dto.talleres.NuevoTaller;
import cl.ucn.app.dto.talleres.TallerResumen;
import cl.ucn.app.exception.RecursoNoEncontradoException;
import cl.ucn.app.exception.ReglaNegocioException;
import cl.ucn.app.model.talleres.Taller;
import cl.ucn.app.repository.talleres.TallerRepository;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

/** Casos de uso del catálogo de talleres: registrar, consultar y filtrar. */
public class TallerService {

    private final TallerRepository tallerRepository;
    private final Clock clock;

    public TallerService(TallerRepository tallerRepository, Clock clock) {
        this.tallerRepository = tallerRepository;
        this.clock = clock;
    }

    /** RF2 y RF3: registrar un taller con su cupo máximo. */
    public Taller registrar(NuevoTaller datos) {
        if (datos == null) {
            throw new ReglaNegocioException("Los datos del taller son obligatorios.");
        }
        String nombre = datos.nombre() == null ? "" : datos.nombre().trim();
        if (nombre.isEmpty()) {
            throw new ReglaNegocioException("El nombre del taller es obligatorio.");
        }
        if (nombre.length() > 120) {
            throw new ReglaNegocioException("El nombre del taller no puede superar los 120 caracteres.");
        }

        String categoria = datos.categoria() == null ? "" : datos.categoria().trim();
        if (categoria.isEmpty()) {
            throw new ReglaNegocioException("La categoría del taller es obligatoria.");
        }
        if (categoria.length() > 50) {
            throw new ReglaNegocioException("La categoría no puede superar los 50 caracteres.");
        }

        if (datos.fecha() == null) {
            throw new ReglaNegocioException("La fecha del taller es obligatoria.");
        }
        if (datos.fecha().isBefore(LocalDate.now(clock))) {
            throw new ReglaNegocioException("La fecha del taller no puede ser anterior a hoy.");
        }

        if (datos.horaInicio() == null || datos.horaFin() == null) {
            throw new ReglaNegocioException("Las horas de inicio y término son obligatorias.");
        }
        if (!datos.horaInicio().isBefore(datos.horaFin())) {
            throw new ReglaNegocioException("La hora de inicio debe ser anterior a la hora de término.");
        }

        if (datos.cupoMaximo() == null || datos.cupoMaximo() <= 0) {
            throw new ReglaNegocioException("El cupo máximo debe ser un número entero mayor a cero.");
        }

        String descripcion = limpiarOpcional(datos.descripcion(), 500, "La descripción no puede superar los 500 caracteres.");
        String lugar = limpiarOpcional(datos.lugar(), 100, "El lugar no puede superar los 100 caracteres.");

        Taller taller = new Taller(
                nombre,
                descripcion,
                categoria.toUpperCase(Locale.ROOT),
                datos.fecha(),
                datos.horaInicio(),
                datos.horaFin(),
                lugar,
                datos.cupoMaximo()
        );
        return tallerRepository.save(taller);
    }

    private String limpiarOpcional(String valor, int maxLen, String mensajeError) {
        if (valor == null) {
            return null;
        }
        String limpio = valor.trim();
        if (limpio.isEmpty()) {
            return null;
        }
        if (limpio.length() > maxLen) {
            throw new ReglaNegocioException(mensajeError);
        }
        return limpio;
    }

    /** RF8 y RF9: talleres disponibles (activos y no pasados), con filtros opcionales. */
    public List<TallerResumen> listarDisponibles(String categoria, LocalDate fecha) {
        return tallerRepository.buscarDisponibles(LocalDate.now(clock), categoria, fecha);
    }

    public TallerResumen obtenerResumen(Long id) {
        return tallerRepository.findResumenById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el taller " + id + "."));
    }

    public List<String> listarCategorias() {
        return tallerRepository.listarCategorias();
    }
}
