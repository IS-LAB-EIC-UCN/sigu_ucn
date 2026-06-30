package cl.ucn.app.service;

import cl.ucn.app.model.Asignatura;
import cl.ucn.app.model.Tutoria;
import cl.ucn.app.model.TutoriaReserva;
import cl.ucn.app.model.Usuario;
import cl.ucn.app.repository.AsignaturaRepository;
import cl.ucn.app.repository.TutoriaRepository;
import cl.ucn.app.repository.TutoriaReservaRepository;
import cl.ucn.app.repository.UsuarioRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class TutoriaService {

    private final TutoriaRepository tutoriaRepository;
    private final TutoriaReservaRepository reservaRepository;
    private final AsignaturaRepository asignaturaRepository;
    private final UsuarioRepository usuarioRepository;

    // Constructor para producción (sin parámetros, crea los repos normalmente)
    public TutoriaService() {
        this.tutoriaRepository = new TutoriaRepository();
        this.reservaRepository = new TutoriaReservaRepository();
        this.asignaturaRepository = new AsignaturaRepository();
        this.usuarioRepository = new UsuarioRepository();
    }

    // Constructor para tests (recibe los mocks por parámetro)
    public TutoriaService(TutoriaRepository tutoriaRepository,
                          TutoriaReservaRepository reservaRepository,
                          AsignaturaRepository asignaturaRepository,
                          UsuarioRepository usuarioRepository) {
        this.tutoriaRepository = tutoriaRepository;
        this.reservaRepository = reservaRepository;
        this.asignaturaRepository = asignaturaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<Tutoria> listarTutorias() {
        return tutoriaRepository.findAll();
    }

    public List<Tutoria> listarDisponibles() {
        return tutoriaRepository.findDisponibles();
    }

    public void crearAsignatura(String codigo, String nombre) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El código de la asignatura es obligatorio.");
        }

        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre de la asignatura es obligatorio.");
        }

        if (asignaturaRepository.findByCodigo(codigo) != null) {
            throw new IllegalArgumentException("Ya existe una asignatura con ese código.");
        }

        Asignatura asignatura = new Asignatura(codigo, nombre);
        asignaturaRepository.save(asignatura);
    }

    public void publicarDisponibilidad(Long tutorId,
                                       Long asignaturaId,
                                       LocalDate fecha,
                                       LocalTime horaInicio,
                                       LocalTime horaFin) {

        if (fecha == null || fecha.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("No se puede publicar una tutoría en fecha pasada.");
        }

        if (horaInicio == null || horaFin == null || !horaFin.isAfter(horaInicio)) {
            throw new IllegalArgumentException("La hora de fin debe ser posterior a la hora de inicio.");
        }

        Usuario tutor = usuarioRepository.findById(tutorId);
        if (tutor == null) {
            throw new IllegalArgumentException("El tutor no existe.");
        }

        Asignatura asignatura = asignaturaRepository.findById(asignaturaId);
        if (asignatura == null) {
            throw new IllegalArgumentException("La asignatura no existe.");
        }

        Tutoria tutoria = new Tutoria(
                tutor,
                asignatura,
                fecha,
                horaInicio,
                horaFin,
                "DISPONIBLE"
        );

        tutoriaRepository.save(tutoria);
    }

    public void reservarTutoria(Long tutoriaId, Long estudianteId) {
        Tutoria tutoria = tutoriaRepository.findById(tutoriaId);

        if (tutoria == null) {
            throw new IllegalArgumentException("La tutoría no existe.");
        }

        if (!"DISPONIBLE".equals(tutoria.getEstado())) {
            throw new IllegalArgumentException("La tutoría no está disponible.");
        }

        Usuario estudiante = usuarioRepository.findById(estudianteId);
        if (estudiante == null) {
            throw new IllegalArgumentException("El estudiante no existe.");
        }

        TutoriaReserva reservaExistente = reservaRepository.findByTutoriaId(tutoriaId);
        if (reservaExistente != null) {
            throw new IllegalArgumentException("Esta franja horaria ya fue reservada.");
        }

        TutoriaReserva reserva = new TutoriaReserva(tutoria, estudiante);
        reservaRepository.save(reserva);

        tutoria.setEstado("RESERVADA");
        tutoriaRepository.update(tutoria);
    }

    public void cancelarTutoria(Long tutoriaId) {
        Tutoria tutoria = tutoriaRepository.findById(tutoriaId);

        if (tutoria == null) {
            throw new IllegalArgumentException("La tutoría no existe.");
        }

        if ("REALIZADA".equals(tutoria.getEstado())) {
            throw new IllegalArgumentException("No se puede cancelar una tutoría ya realizada.");
        }

        tutoria.setEstado("CANCELADA");
        tutoriaRepository.update(tutoria);
    }

    public void registrarAsistencia(Long reservaId, boolean asistio) {
        TutoriaReserva reserva = reservaRepository.findById(reservaId);

        if (reserva == null) {
            throw new IllegalArgumentException("La reserva no existe.");
        }

        reserva.setAsistio(asistio);
        reservaRepository.update(reserva);

        Tutoria tutoria = reserva.getTutoria();
        tutoria.setEstado("REALIZADA");
        tutoriaRepository.update(tutoria);
    }

    public List<TutoriaReserva> listarHistorialEstudiante(Long estudianteId) {
        return reservaRepository.findByEstudiante(estudianteId);
    }

    public List<TutoriaReserva> listarReservas() {
        return reservaRepository.findAll();
    }

    public List<Asignatura> listarAsignaturas() {
        return asignaturaRepository.findAll();
    }
    public List<Tutoria> listarTutoriasPorTutor(Long tutorId) {
        return tutoriaRepository.findByTutor(tutorId);
    }

    public List<TutoriaReserva> listarReservasPorTutor(Long tutorId) {
        return reservaRepository.findByTutor(tutorId);
    }
    public List<Tutoria> filtrarTutorias(Long tutorId,
                                         Long asignaturaId,
                                         LocalDate fecha,
                                         String estado) {

        return tutoriaRepository.findFiltradas(
                tutorId,
                asignaturaId,
                fecha,
                estado
        );
    }

    public List<Usuario> listarTutores() {
        return usuarioRepository.findByRol("TUTOR");
    }

    public void registrarUsuarioTutoria(String nombre,
                                        String correo,
                                        String password,
                                        String rolNombre) {

        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("Debe ingresar un nombre.");
        }

        if (correo == null || correo.isBlank()) {
            throw new IllegalArgumentException("Debe ingresar un correo.");
        }

        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Debe ingresar una contraseña.");
        }

        if (usuarioRepository.findByCorreo(correo) != null) {
            throw new IllegalArgumentException("Ya existe un usuario con ese correo.");
        }

        var rol = usuarioRepository.findRolByNombre(rolNombre);

        if (rol == null) {
            throw new IllegalArgumentException("No existe el rol.");
        }

        Usuario usuario = new Usuario(
                nombre,
                correo,
                password,
                true,
                rol
        );

        usuarioRepository.save(usuario);
    }

    public List<Usuario> listarEstudiantes() {
        return usuarioRepository.findByRol("ESTUDIANTE");
    }

}