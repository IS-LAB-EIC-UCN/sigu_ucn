package cl.ucn.app.service;

import cl.ucn.app.dto.TutoriaFiltro;
import cl.ucn.app.dto.TutoriaListado;
import cl.ucn.app.model.Tutoria;
import cl.ucn.app.repository.InscripcionTutoriaRepository;
import cl.ucn.app.repository.TutoriaRepository;

import java.time.LocalDateTime;
import java.util.List;

public class TutoriaBusquedaService {

    private final TutoriaRepository tutoriaRepository;
    private final InscripcionTutoriaRepository inscripcionRepository;

    public TutoriaBusquedaService(TutoriaRepository tutoriaRepository,
                                  InscripcionTutoriaRepository inscripcionRepository) {
        this.tutoriaRepository = tutoriaRepository;
        this.inscripcionRepository = inscripcionRepository;
    }

    public List<TutoriaListado> buscar(TutoriaFiltro filtro) {
        return tutoriaRepository.buscarDisponibles(filtro, LocalDateTime.now()).stream()
                .map(this::convertir)
                .filter(item -> item.cuposDisponibles() > 0)
                .toList();
    }

    private TutoriaListado convertir(Tutoria tutoria) {
        long ocupados = inscripcionRepository.countActivasByTutoriaId(tutoria.getId());
        long disponibles = Math.max(0, tutoria.getCapacidad() - ocupados);
        return new TutoriaListado(tutoria, disponibles);
    }
}
