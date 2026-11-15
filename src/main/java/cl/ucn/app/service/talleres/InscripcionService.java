package cl.ucn.app.service.talleres;

import cl.ucn.app.dto.talleres.ListadoInscritos;
import cl.ucn.app.dto.talleres.ResultadoAnulacion;
import cl.ucn.app.dto.talleres.ResultadoInscripcion;
import cl.ucn.app.exception.AccesoDenegadoException;
import cl.ucn.app.exception.RecursoNoEncontradoException;
import cl.ucn.app.exception.ReglaNegocioException;
import cl.ucn.app.model.Usuario;
import cl.ucn.app.model.talleres.EstadoInscripcion;
import cl.ucn.app.model.talleres.Inscripcion;
import cl.ucn.app.model.talleres.Taller;
import cl.ucn.app.repository.talleres.InscripcionRepository;
import cl.ucn.app.repository.talleres.ParticipanteRepository;
import cl.ucn.app.repository.talleres.TallerRepository;
import cl.ucn.app.service.talleres.reglas.ReglaInscripcion;
import cl.ucn.app.service.talleres.reglas.SolicitudInscripcion;

import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/** Casos de uso de inscripción: inscribir, anular (con promoción desde la espera) y listar. */
public class InscripcionService {

    private final InscripcionRepository inscripcionRepository;
    private final TallerRepository tallerRepository;
    private final ParticipanteRepository participanteRepository;
    private final List<ReglaInscripcion> reglas;
    private final PoliticaCupo politicaCupo;
    private final Clock clock;

    public InscripcionService(InscripcionRepository inscripcionRepository,
                              TallerRepository tallerRepository,
                              ParticipanteRepository participanteRepository,
                              List<ReglaInscripcion> reglas,
                              PoliticaCupo politicaCupo,
                              Clock clock) {
        this.inscripcionRepository = inscripcionRepository;
        this.tallerRepository = tallerRepository;
        this.participanteRepository = participanteRepository;
        this.reglas = List.copyOf(reglas);
        this.politicaCupo = politicaCupo;
        this.clock = clock;
    }

    /**
     * RF4: inscribir un participante. Aplica todas las reglas; si el taller está lleno
     * la inscripción queda EN_ESPERA (RF7).
     */
    public ResultadoInscripcion inscribir(Long tallerId, Long usuarioId) {
        Taller taller = tallerRepository.findById(tallerId)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el taller " + tallerId + "."));
        Usuario participante = participanteRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el participante " + usuarioId + "."));

        List<Inscripcion> vigentes = inscripcionRepository.findVigentesByUsuario(usuarioId);
        SolicitudInscripcion solicitud = new SolicitudInscripcion(taller, participante, vigentes, LocalDate.now(clock));
        for (ReglaInscripcion regla : reglas) {
            regla.validar(solicitud);
        }

        long inscritos = inscripcionRepository.countByTallerAndEstado(tallerId, EstadoInscripcion.INSCRITO);
        EstadoInscripcion estado = politicaCupo.estadoParaNuevaInscripcion(taller, inscritos);

        Inscripcion guardada = inscripcionRepository.save(new Inscripcion(taller, participante, estado));

        int posicion = 0;
        if (estado == EstadoInscripcion.EN_ESPERA) {
            posicion = (int) inscripcionRepository.countByTallerAndEstado(tallerId, EstadoInscripcion.EN_ESPERA);
        }
        return new ResultadoInscripcion(guardada.getId(), estado, posicion);
    }

    /**
     * RF5: anular una inscripción. Solo puede hacerlo su dueño o un administrador.
     * Si se liberó un cupo, el primero de la lista de espera pasa a INSCRITO (RF7).
     */
    public ResultadoAnulacion anular(Long inscripcionId, Long usuarioId, boolean esAdmin) {
        Inscripcion inscripcion = inscripcionRepository.findById(inscripcionId)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la inscripción " + inscripcionId + "."));

        if (!esAdmin && !Objects.equals(inscripcion.getUsuario().getId(), usuarioId)) {
            throw new AccesoDenegadoException("No tienes permiso para anular esta inscripción.");
        }

        if (inscripcion.getEstado() == EstadoInscripcion.ANULADA) {
            throw new ReglaNegocioException("La inscripción ya estaba anulada.");
        }

        boolean liberabaCupo = inscripcion.getEstado() == EstadoInscripcion.INSCRITO;
        inscripcion.anular();

        List<Inscripcion> porActualizar = new ArrayList<>();
        porActualizar.add(inscripcion);

        String promovidoNombre = null;
        if (liberabaCupo) {
            Optional<Inscripcion> siguiente = inscripcionRepository.findPrimeraEnEspera(inscripcion.getTaller().getId());
            if (siguiente.isPresent()) {
                Inscripcion promovida = siguiente.get();
                promovida.promoverDesdeEspera();
                porActualizar.add(promovida);
                promovidoNombre = promovida.getUsuario().getNombre();
            }
        }

        inscripcionRepository.updateAll(porActualizar);
        return new ResultadoAnulacion(promovidoNombre);
    }

    /** RF6 y RF7: inscritos y lista de espera de un taller, en orden de llegada. */
    public ListadoInscritos listarInscritos(Long tallerId) {
        // TODO [Tarea 4 - RF6]: implementar.
        //  - Buscar el taller con tallerRepository.findById (si no existe, RecursoNoEncontradoException).
        //  - Usar inscripcionRepository.findByTallerAndEstado(tallerId, INSCRITO) y (..., EN_ESPERA).
        //  - Devolver new ListadoInscritos(taller, inscritos, enEspera).
        throw new UnsupportedOperationException("TODO Tarea 4: InscripcionService.listarInscritos()");
    }

    /** Historial de inscripciones del participante (incluye anuladas). */
    public List<Inscripcion> misInscripciones(Long usuarioId) {
        return inscripcionRepository.findByUsuario(usuarioId);
    }
}
