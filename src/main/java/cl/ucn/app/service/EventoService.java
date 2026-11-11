package cl.ucn.app.service;
import cl.ucn.app.model.Evento;
import cl.ucn.app.repository.EventoStore;
import java.time.*;

/** Reglas de eventos, independientes de HTTP y de la implementación de persistencia. */
public class EventoService {
    private final EventoStore eventos;
    private final Clock reloj;
    public EventoService(EventoStore eventos) { this(eventos, Clock.systemDefaultZone()); }
    public EventoService(EventoStore eventos, Clock reloj) { this.eventos = eventos; this.reloj = reloj; }
    public void validar(Evento e) {
        if (e.getTitulo() == null || e.getTitulo().isBlank() || e.getTitulo().length() > 150)
            throw new IllegalArgumentException("El título es obligatorio y admite hasta 150 caracteres.");
        if (e.getDescripcion() != null && e.getDescripcion().length() > 5000)
            throw new IllegalArgumentException("La descripci\u00f3n admite hasta 5000 caracteres.");
        if (e.getFecha() == null || e.getHoraInicio() == null || e.getHoraFin() == null)
            throw new IllegalArgumentException("Completa la fecha y las horas del evento.");
        if (!LocalDateTime.of(e.getFecha(), e.getHoraInicio()).isAfter(LocalDateTime.now(reloj)))
            throw new IllegalArgumentException("El evento debe comenzar en una fecha y hora futuras.");
        if (!e.getHoraFin().isAfter(e.getHoraInicio()))
            throw new IllegalArgumentException("La hora de término debe ser posterior al inicio.");
        if (e.getCupoMaximo() == null || e.getCupoMaximo() < 1)
            throw new IllegalArgumentException("El cupo debe ser mayor que cero.");
        if (e.getEspacio() == null || !Boolean.TRUE.equals(e.getEspacio().getDisponible()))
            throw new IllegalArgumentException("Selecciona una ubicación disponible.");
        if (e.getCupoMaximo() > e.getEspacio().getCapacidad())
            throw new IllegalArgumentException("El cupo supera la capacidad de la ubicación.");
        if (e.getExpositor() == null) throw new IllegalArgumentException("Selecciona un expositor.");
        if (e.getTematica().isBlank() || e.getTematica().length() > 100)
            throw new IllegalArgumentException("La temática admite entre 1 y 100 caracteres.");
        e.setTitulo(e.getTitulo().strip()); e.setLugar(e.getEspacio().getNombre());
    }
    public void guardar(Evento e) { validar(e); if (e.getId() == null) eventos.save(e); else eventos.update(e); }
    public void publicar(Evento e) {
        if (!"BORRADOR".equals(e.getEstado())) throw new IllegalArgumentException("Solo se publican eventos en borrador.");
        validar(e); e.setEstado("PUBLICADO"); eventos.update(e);
    }
    public void reactivar(Evento e) {
        if (!"CANCELADO".equals(e.getEstado()))
            throw new IllegalArgumentException("Solo se pueden reactivar eventos cancelados.");
        eventos.reactivar(e.getId());
        e.setEstado("BORRADOR");
    }
    public void cancelar(Evento e) {
        if ("CANCELADO".equals(e.getEstado())) throw new IllegalArgumentException("El evento ya está cancelado.");
        e.setEstado("CANCELADO"); eventos.update(e);
    }
}
