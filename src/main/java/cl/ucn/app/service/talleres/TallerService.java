package cl.ucn.app.service.talleres;

import cl.ucn.app.dto.talleres.NuevoTaller;
import cl.ucn.app.dto.talleres.TallerResumen;
import cl.ucn.app.exception.RecursoNoEncontradoException;
import cl.ucn.app.model.talleres.Taller;
import cl.ucn.app.repository.talleres.TallerRepository;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

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
        // TODO [Tarea 1 - RF2 y RF3]: implementar.
        //  1. Validar y lanzar ReglaNegocioException con un mensaje claro si:
        //     - nombre vacío o de más de 120 caracteres;
        //     - categoría vacía;
        //     - fecha u horas nulas;
        //     - fecha anterior a LocalDate.now(clock)  (usar el clock, así se puede probar);
        //     - horaInicio no es anterior a horaFin;
        //     - cupoMaximo nulo o <= 0.
        //  2. Crear new Taller(...) (categoría en mayúsculas, textos vacíos como null).
        //  3. return tallerRepository.save(taller);
        throw new UnsupportedOperationException("TODO Tarea 1: TallerService.registrar()");
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
