package cl.ucn.app.service;

import cl.ucn.app.model.Inscripcion;
import cl.ucn.app.model.Taller;
import cl.ucn.app.model.Usuario;
import cl.ucn.app.repository.InscripcionRepository;
import cl.ucn.app.repository.TallerRepository;
import cl.ucn.app.repository.UsuarioRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class TallerService {

    private final TallerRepository tallerRepository;
    private final InscripcionRepository inscripcionRepository;
    private final UsuarioRepository usuarioRepository;

    public TallerService() {
        this.tallerRepository = new TallerRepository();
        this.inscripcionRepository = new InscripcionRepository();
        this.usuarioRepository = new UsuarioRepository();
    }

    public TallerService(TallerRepository tallerRepository, InscripcionRepository inscripcionRepository, UsuarioRepository usuarioRepository) {
        this.tallerRepository = tallerRepository;
        this.inscripcionRepository = inscripcionRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public Taller crearTaller(String nombre, String descripcion, Integer cupos, LocalDate inicio, LocalDate fin, Character bloque, Long profesorId) throws Exception {
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

        Taller taller = new Taller(nombre, descripcion, cupos, inicio, fin, "ABIERTO", bloque, profesor, null);
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

    //Lista de espera
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

    //visibilidad de rol
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
}
