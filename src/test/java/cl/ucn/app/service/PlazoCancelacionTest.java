package cl.ucn.app.service;
import cl.ucn.app.model.Evento;
import org.junit.jupiter.api.Test;
import java.time.*;
import static org.junit.jupiter.api.Assertions.*;
class PlazoCancelacionTest {
    private Evento evento() {
        Evento e=new Evento();e.setFecha(LocalDate.of(2026,11,20));e.setHoraInicio(LocalTime.of(10,0));e.setEstado("PUBLICADO");return e;
    }
    private Clock reloj(String hora) { return Clock.fixed(Instant.parse("2026-11-20T"+hora+"Z"),ZoneOffset.UTC); }
    @Test void permiteUnSegundoAntes() { assertDoesNotThrow(()->PlazoCancelacion.validar(evento(),reloj("09:59:59"))); }
    @Test void rechazaDesdeElInicio() {
        assertThrows(IllegalStateException.class,()->PlazoCancelacion.validar(evento(),reloj("10:00:00")));
        assertThrows(IllegalStateException.class,()->PlazoCancelacion.validar(evento(),reloj("10:00:01")));
    }
    @Test void rechazaEventoCanceladoAunqueNoComience() {
        Evento e=evento();e.setEstado("CANCELADO");assertThrows(IllegalStateException.class,()->PlazoCancelacion.validar(e,reloj("09:00:00")));
    }
}
