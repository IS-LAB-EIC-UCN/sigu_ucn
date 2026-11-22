package cl.ucn.app.service;
import cl.ucn.app.model.Evento;
import java.time.*;
/** RF-04: la cancelación se cierra exactamente al comenzar el evento. */
public final class PlazoCancelacion {
    private PlazoCancelacion() {}
    public static void validar(Evento evento, Clock reloj) {
        if (!"PUBLICADO".equals(evento.getEstado())) throw new IllegalStateException("El evento no admite cancelaciones de inscripción.");
        LocalDateTime inicio=LocalDateTime.of(evento.getFecha(),evento.getHoraInicio());
        if (!LocalDateTime.now(reloj).isBefore(inicio))
            throw new IllegalStateException("El plazo de cancelación terminó al comenzar el evento.");
    }
}
