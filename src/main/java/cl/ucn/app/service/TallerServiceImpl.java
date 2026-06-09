package cl.ucn.app.service;

import cl.ucn.app.model.Espacio;
import cl.ucn.app.model.Inscripcion;
import cl.ucn.app.model.Taller;
import cl.ucn.app.model.Usuario;
import cl.ucn.app.repository.EspacioRepository;
import cl.ucn.app.repository.InscripcionRepository;
import cl.ucn.app.repository.UsuarioRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import cl.ucn.app.repository.ITallerRepository;
import cl.ucn.app.repository.TallerRepositoryImpl;

public class TallerServiceImpl implements ITallerService {

    private final ITallerRepository tallerRepository;
    private final InscripcionRepository inscripcionRepository;
    private final UsuarioRepository usuarioRepository;
    private final EspacioRepository espacioRepository;

    public TallerServiceImpl() {
        this.tallerRepository = new TallerRepositoryImpl();
        this.inscripcionRepository = new InscripcionRepository();
        this.usuarioRepository = new UsuarioRepository();
        this.espacioRepository = new EspacioRepository();
    }

    public TallerServiceImpl(ITallerRepository tallerRepository, InscripcionRepository inscripcionRepository,
            UsuarioRepository usuarioRepository, EspacioRepository espacioRepository) {
        this.tallerRepository = tallerRepository;
        this.inscripcionRepository = inscripcionRepository;
        this.usuarioRepository = usuarioRepository;
        this.espacioRepository = espacioRepository;
    }

    public Taller crearTaller(String nombre, String descripcion, String categoria, Integer cupos, LocalDate inicio, LocalDate fin,
            Character bloque, Long profesorId, Long espacioId) throws Exception {
        if (cupos <= 0) {
            throw new Exception("Los cupos deben ser mayores a cero.");
        }
        if (inicio.isAfter(fin)) {
            throw new Exception("La fecha de inicio no puede ser después de la fecha de fin.");
        }

        Usuario profesor = usuarioRepository.findById(profesorId);
        if (profesor == null || !profesor.getRol().getNombre().equals("DOCENTE")) {
            throw new Exception("El profesor asignado no existe o no es Docente.");
        }

        if (inicio.isBefore(LocalDate.now())) {
            throw new Exception("Fecha de inicio invalida");
        }

        Espacio espacio = null;
        if (espacioId != null) {
            espacio = espacioRepository.findById(espacioId);
            if (espacio == null) {
                throw new Exception("El espacio asignado no existe.");
            }
            if (tallerRepository.existeConflicto(espacioId, bloque, inicio, fin)) {
                throw new Exception("Conflicto de horario: Ya existe un taller en el espacio '" + espacio.getNombre() + "' en el bloque " + bloque + " durante estas fechas.");
            }
        }

        Taller taller = new Taller(nombre, descripcion, categoria, cupos, inicio, fin, "ABIERTO", bloque, profesor, espacio);
        tallerRepository.save(taller);
        return taller;

    }

    public String inscribirAlumno(Long tallerId, Long usuarioId) throws Exception {
        Taller taller = tallerRepository.findById(tallerId);
        Usuario alumno = usuarioRepository.findById(usuarioId);

        if (taller == null || alumno == null) {
            throw new Exception("Taller o alumno no encontrados.");
        }

        if (!taller.getEstado().equals("ABIERTO")) {
            throw new Exception("El taller no está disponible para inscripciones.");
        }

        Inscripcion existente = inscripcionRepository.findByTallerAndUsuario(tallerId, usuarioId);
        if (existente != null && !existente.getEstado().equals("CANCELADO")) {
            throw new Exception("Ya tienes estado '" + existente.getEstado() + "' en este taller.");
        }

        List<Inscripcion> misInscripciones = inscripcionRepository.findByUsuario(usuarioId);
        for (Inscripcion ins : misInscripciones) {
            if (ins.getEstado().equals("INSCRITO")) {
                Taller tInscrito = ins.getTaller();
                if (tInscrito.getBloqueHorario().equals(taller.getBloqueHorario())) {
                    if (!taller.getFechaInicio().isAfter(tInscrito.getFechaFin()) && 
                        !taller.getFechaFin().isBefore(tInscrito.getFechaInicio())) {
                        throw new Exception("No puedes inscribirte: Choque de horario con el taller '" + tInscrito.getNombre() + "' en el bloque " + taller.getBloqueHorario() + ".");
                    }
                }
            }
        }

        Inscripcion inscripcion = (existente != null) ? existente : new Inscripcion();
        inscripcion.setTaller(taller);
        inscripcion.setUsuario(alumno);
        inscripcion.setFechaInscripcion(LocalDateTime.now());

        // Cupos al inscribirse
        Long inscritosActuales = inscripcionRepository.countByTallerAndEstado(tallerId, "INSCRITO");

        String mensaje;
        if (inscritosActuales < taller.getCuposTotales()) {
            inscripcion.setEstado("INSCRITO");
            mensaje = "¡Inscripción exitosa! Lograste conseguir un cupo.";
        } else {
            inscripcion.setEstado("EN_ESPERA");
            mensaje = "El taller está lleno. Has quedado en Lista de Espera.";
        }

        inscripcionRepository.save(inscripcion);
        return mensaje;
    }

    // Lista de espera
    public void cancelarInscripcion(Long tallerId, Long usuarioId) throws Exception {
        Inscripcion inscripcion = inscripcionRepository.findByTallerAndUsuario(tallerId, usuarioId);

        if (inscripcion == null || inscripcion.getEstado().equals("CANCELADO")) {
            throw new Exception("No tienes una inscripción activa para cancelar.");
        }

        boolean teniaCupo = inscripcion.getEstado().equals("INSCRITO");

        inscripcion.setEstado("CANCELADO");
        inscripcionRepository.save(inscripcion);

        if (teniaCupo) {
            Inscripcion afortunado = inscripcionRepository.findFirstEnEspera(tallerId);
            if (afortunado != null) {
                afortunado.setEstado("INSCRITO");
                inscripcionRepository.save(afortunado);
            }
        }
    }

    // visibilidad de rol
    public List<Taller> obtenerTalleres(Long usuarioId, String rol) {
        List<Taller> todos = tallerRepository.findAll();

        if (rol.equals("DOCENTE")) {
            return todos.stream()
                    .filter(t -> t.getProfesor().getId().equals(usuarioId))
                    .collect(Collectors.toList());
        }

        return todos;
    }

    public List<Inscripcion> obtenerMisInscripciones(Long usuarioId) {
        return inscripcionRepository.findByUsuario(usuarioId);
    }

    public List<Inscripcion> obtenerInscripcionesPorTaller(Long tallerId, Long docenteId) throws Exception {
        Taller taller = tallerRepository.findById(tallerId);
        if (taller == null) {
            throw new Exception("El taller no existe.");
        }
        if (!taller.getProfesor().getId().equals(docenteId)) {
            throw new Exception("No tienes permiso para ver los alumnos de este taller.");
        }
        return inscripcionRepository.findByTallerId(tallerId);
    }

    public List<Usuario> obtenerDocentes() {
        return usuarioRepository.findByRol("DOCENTE");
    }

    public List<Espacio> obtenerEspacios() {
        return espacioRepository.findAll();
    }

    public void eliminarTaller(Long tallerId) throws Exception {
        Taller taller = tallerRepository.findById(tallerId);
        if (taller == null) {
            throw new Exception("El taller no existe.");
        }
        inscripcionRepository.deleteByTallerId(tallerId);
        tallerRepository.delete(tallerId);
    }

    public Taller obtenerTallerPorId(Long id) throws Exception {
        Taller taller = tallerRepository.findById(id);
        if (taller == null) {
            throw new Exception("El taller no existe.");
        }
        return taller;
    }

    public void editarTaller(Long tallerId, String nombre, String descripcion, String categoria, Integer cupos, LocalDate inicio, LocalDate fin, Character bloque, Long profesorId, Long espacioId) throws Exception {
        Taller taller = tallerRepository.findById(tallerId);
        if (taller == null) {
            throw new Exception("El taller no existe.");
        }

        if (cupos <= 0) {
            throw new Exception("Los cupos deben ser mayores a cero.");
        }

        Long inscritos = inscripcionRepository.countByTallerAndEstado(tallerId, "INSCRITO");
        if (cupos < inscritos) {
            throw new Exception("No puedes reducir los cupos a un valor menor que la cantidad de alumnos ya inscritos (" + inscritos + ").");
        }

        if (inicio.isAfter(fin)) {
            throw new Exception("La fecha de inicio no puede ser después de la fecha de fin.");
        }

        Usuario profesor = usuarioRepository.findById(profesorId);
        if (profesor == null || !profesor.getRol().getNombre().equals("DOCENTE")) {
            throw new Exception("El profesor asignado no es válido.");
        }

        Espacio espacio = (espacioId != null) ? espacioRepository.findById(espacioId) : null;
        if (espacioId != null && espacio == null) {
            throw new Exception("El espacio asignado no es válido.");
        }

        if (espacio != null) {
            boolean cambioClave = taller.getEspacio() == null || !taller.getEspacio().getId().equals(espacioId) || 
                                  !taller.getBloqueHorario().equals(bloque) || 
                                  !taller.getFechaInicio().equals(inicio) || 
                                  !taller.getFechaFin().equals(fin);
            if (cambioClave && tallerRepository.existeConflicto(espacioId, bloque, inicio, fin)) {
                throw new Exception("El espacio seleccionado ya está ocupado en ese bloque y rango de fechas.");
            }
        }

        taller.setNombre(nombre);
        taller.setDescripcion(descripcion);
        taller.setCategoria(categoria);
        taller.setCuposTotales(cupos);
        taller.setFechaInicio(inicio);
        taller.setFechaFin(fin);
        taller.setBloqueHorario(bloque);
        taller.setProfesor(profesor);
        taller.setEspacio(espacio);

        tallerRepository.save(taller);
    }
}
