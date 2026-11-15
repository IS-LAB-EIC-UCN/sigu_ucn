package cl.ucn.app.service;

import cl.ucn.app.model.Estudiante;
import cl.ucn.app.model.InscripcionTutoria;
import cl.ucn.app.model.Tutoria;
import cl.ucn.app.repository.EstudianteRepository;
import cl.ucn.app.repository.InscripcionTutoriaRepository;
import cl.ucn.app.repository.TutoriaRepository;

import java.util.List;

public class AgendaService {

    private final EstudianteRepository estudianteRepository;
    private final InscripcionTutoriaRepository inscripcionRepository;
    private final TutoriaRepository tutoriaRepository;

    public AgendaService(EstudianteRepository estudianteRepository,
                         InscripcionTutoriaRepository inscripcionRepository,
                         TutoriaRepository tutoriaRepository) {
        this.estudianteRepository = estudianteRepository;
        this.inscripcionRepository = inscripcionRepository;
        this.tutoriaRepository = tutoriaRepository;
    }

    public List<InscripcionTutoria> agendaEstudiante(Long usuarioId) {
        Estudiante estudiante = estudianteRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new TutoriaValidacionException("usuario", "No existe el perfil de estudiante."));
        return inscripcionRepository.findByEstudianteId(estudiante.getId());
    }

    public List<Tutoria> agendaTutor(Long tutorId) {
        return tutoriaRepository.findByTutorId(tutorId);
    }
}
