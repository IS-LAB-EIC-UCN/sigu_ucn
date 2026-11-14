package cl.ucn.app.service;

import cl.ucn.app.dto.TutorForm;
import cl.ucn.app.model.Asignatura;
import cl.ucn.app.model.Estudiante;
import cl.ucn.app.model.Tutor;
import cl.ucn.app.repository.AsignaturaRepository;
import cl.ucn.app.repository.EstudianteRepository;
import cl.ucn.app.repository.TutorRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class TutorService {

    private final TutorRepository tutorRepository;
    private final EstudianteRepository estudianteRepository;
    private final AsignaturaRepository asignaturaRepository;

    public TutorService(TutorRepository tutorRepository,
                        EstudianteRepository estudianteRepository,
                        AsignaturaRepository asignaturaRepository) {
        this.tutorRepository = tutorRepository;
        this.estudianteRepository = estudianteRepository;
        this.asignaturaRepository = asignaturaRepository;
    }

    public List<Tutor> listar() {
        return tutorRepository.findAll();
    }

    public Tutor registrar(TutorForm form) {
        Estudiante estudiante = buscarEstudianteHabilitable(form.correo());
        List<Asignatura> asignaturas = buscarAsignaturas(form.asignaturaIds());

        Tutor tutor = new Tutor(estudiante, new HashSet<>(asignaturas));
        tutorRepository.save(tutor);
        return tutor;
    }

    private Estudiante buscarEstudianteHabilitable(String valor) {
        String correo = valor == null ? "" : valor.trim().toLowerCase();
        if (correo.isEmpty()) {
            throw new TutoriaValidacionException("correo", "Debe ingresar el correo institucional del estudiante.");
        }

        Estudiante estudiante = estudianteRepository.findByCorreo(correo).orElseThrow(
                () -> new TutoriaValidacionException("correo", "No existe un estudiante con el correo " + correo + ".")
        );

        if (!estudiante.estaRegistrado()) {
            throw new TutoriaValidacionException("correo",
                    "El estudiante aún no tiene cuenta en el sistema. Debe registrarse antes de ser tutor.");
        }
        if (tutorRepository.existsByEstudianteId(estudiante.getId())) {
            throw new TutoriaValidacionException("correo", "Ya existe un tutor con el correo " + correo + ".");
        }
        return estudiante;
    }

    private List<Asignatura> buscarAsignaturas(List<String> valores) {
        List<Long> ids = new ArrayList<>();
        for (String valor : valores) {
            try {
                Long id = Long.parseLong(valor);
                if (!ids.contains(id)) {
                    ids.add(id);
                }
            } catch (NumberFormatException e) {
                throw new TutoriaValidacionException("asignaturas", "La selección de asignaturas no es válida.");
            }
        }

        if (ids.isEmpty()) {
            throw new TutoriaValidacionException("asignaturas", "Debe seleccionar al menos una asignatura.");
        }

        List<Asignatura> asignaturas = asignaturaRepository.findByIds(ids);
        if (asignaturas.size() != ids.size()) {
            throw new TutoriaValidacionException("asignaturas", "Una de las asignaturas seleccionadas no existe.");
        }
        return asignaturas;
    }
}
