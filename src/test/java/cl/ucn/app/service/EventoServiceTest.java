package cl.ucn.app.service;
import cl.ucn.app.model.*;
import cl.ucn.app.repository.EventoStore;
import java.time.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EventoServiceTest {
    private final EventoStore store=mock(EventoStore.class);
    private final EventoService service=new EventoService(store,Clock.fixed(Instant.parse("2026-11-11T12:00:00Z"),ZoneOffset.UTC));
    private Evento evento;
    @BeforeEach void preparar() {
        evento=new Evento();evento.setTitulo("Seminario");evento.setTematica("Ciencia");evento.setCategoria(new Categoria("Ciencia"));
        evento.setFecha(LocalDate.of(2026,12,1));evento.setHoraInicio(LocalTime.of(10,0));evento.setHoraFin(LocalTime.of(11,0));
        evento.setCupoMaximo(20);evento.setEspacio(new Espacio("Sala 1","EVENTOS",30,true));evento.setExpositor(new Expositor());
    }
    @Test void guardaEventoValido() { service.guardar(evento);verify(store).save(evento);assertEquals("Sala 1",evento.getLugar()); }
    @Test void rechazaTituloVacio() { evento.setTitulo(" ");assertThrows(IllegalArgumentException.class,()->service.guardar(evento));verifyNoInteractions(store); }
    @Test void rechazaFechaPasada() { evento.setFecha(LocalDate.of(2026,11,10));assertThrows(IllegalArgumentException.class,()->service.guardar(evento)); }
    @Test void rechazaHorarioInvertido() { evento.setHoraFin(LocalTime.of(9,0));assertThrows(IllegalArgumentException.class,()->service.guardar(evento)); }
    @Test void rechazaCupoCero() { evento.setCupoMaximo(0);assertThrows(IllegalArgumentException.class,()->service.guardar(evento)); }
    @Test void rechazaExcesoCapacidad() { evento.setCupoMaximo(31);assertThrows(IllegalArgumentException.class,()->service.guardar(evento)); }
    @Test void rechazaSalaNoDisponibleYExpositorAusente() {
        evento.getEspacio().setDisponible(false);assertThrows(IllegalArgumentException.class,()->service.guardar(evento));
        evento.getEspacio().setDisponible(true);evento.setExpositor(null);assertThrows(IllegalArgumentException.class,()->service.guardar(evento));
    }
    @Test void controlaTransicionesDeEstado() {
        service.publicar(evento);assertEquals("PUBLICADO",evento.getEstado());verify(store).update(evento);
        assertThrows(IllegalArgumentException.class,()->service.publicar(evento));
        service.cancelar(evento);assertEquals("CANCELADO",evento.getEstado());
        assertThrows(IllegalArgumentException.class,()->service.publicar(evento));
        assertThrows(IllegalArgumentException.class,()->service.cancelar(evento));
        service.reactivar(evento); assertEquals("BORRADOR",evento.getEstado());
        verify(store).reactivar(evento.getId());
        assertThrows(IllegalArgumentException.class,()->service.reactivar(evento));
        service.publicar(evento); assertEquals("PUBLICADO",evento.getEstado());
    }
}
