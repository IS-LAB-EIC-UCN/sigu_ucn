package cl.ucn.app.service;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class EstacionamientoServiceTest {

    private final EstacionamientoService service = new EstacionamientoService();

    @Test
    void noDebeReservarFechaPasada() {
        assertThrows(IllegalArgumentException.class, () ->
                service.reservarEspacio(2L, 5L, 1L, 1,
                        LocalDate.now().minusDays(1),
                        LocalTime.of(10, 0),
                        LocalTime.of(11, 0))
        );
    }

    @Test
    void noDebeReservarHoraPasadaDeHoy() {
        assertThrows(IllegalArgumentException.class, () ->
                service.reservarEspacio(2L, 5L, 1L, 2,
                        LocalDate.now(),
                        LocalTime.now().minusMinutes(5),
                        LocalTime.now().plusHours(1))
        );
    }

    @Test
    void noDebePermitirHoraFinAntesDeInicio() {
        assertThrows(IllegalArgumentException.class, () ->
                service.reservarEspacio(2L, 5L, 1L, 3,
                        LocalDate.now().plusDays(5),
                        LocalTime.of(15, 0),
                        LocalTime.of(14, 0))
        );
    }

    @Test
    void noDebePermitirPuestoMenorAUno() {
        assertThrows(IllegalArgumentException.class, () ->
                service.reservarEspacio(2L, 5L, 1L, 0,
                        LocalDate.now().plusDays(6),
                        LocalTime.of(10, 0),
                        LocalTime.of(11, 0))
        );
    }

    @Test
    void noDebePermitirPuestoMayorACapacidad() {
        assertThrows(IllegalArgumentException.class, () ->
                service.reservarEspacio(2L, 5L, 1L, 999,
                        LocalDate.now().plusDays(7),
                        LocalTime.of(10, 0),
                        LocalTime.of(11, 0))
        );
    }

    @Test
    void noDebePermitirVehiculoDeOtroUsuario() {
        assertThrows(IllegalStateException.class, () ->
                service.reservarEspacio(2L, 5L, 2L, 4,
                        LocalDate.now().plusDays(8),
                        LocalTime.of(10, 0),
                        LocalTime.of(11, 0))
        );
    }

    @Test
    void noDebePermitirEspacioQueNoEsEstacionamiento() {
        assertThrows(IllegalArgumentException.class, () ->
                service.reservarEspacio(2L, 1L, 1L, 1,
                        LocalDate.now().plusDays(9),
                        LocalTime.of(10, 0),
                        LocalTime.of(11, 0))
        );
    }

    @Test
    void noDebeRegistrarCorreoDuplicado() {
        assertThrows(IllegalArgumentException.class, () ->
                service.registrarUsuarioConVehiculo(
                        "Usuario Repetido",
                        "admin@sigu.cl",
                        "test123",
                        "DOCENTE",
                        "ZZZZ99",
                        "Toyota",
                        "Yaris")
        );
    }

    @Test
    void noDebeRegistrarPatenteDuplicada() {
        String correoUnico = "test" + System.currentTimeMillis() + "@sigu.cl";

        assertThrows(IllegalArgumentException.class, () ->
                service.registrarUsuarioConVehiculo(
                        "Usuario Test",
                        correoUnico,
                        "test123",
                        "DOCENTE",
                        "ABCD12",
                        "Toyota",
                        "Yaris")
        );
    }
}