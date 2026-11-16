
package cl.ucn.app.service;

import cl.ucn.app.model.Espacio;
//
import cl.ucn.app.model.EstadoReserva;
//
import cl.ucn.app.model.Reserva;
import cl.ucn.app.model.Usuario;
import cl.ucn.app.repository.EspacioRepository;
import cl.ucn.app.repository.ReservaRepository;
import cl.ucn.app.repository.UsuarioRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CrearReservaServiceTest {

    private static final Long USUARIO_ID = 3L;
    private static final Long ESPACIO_ID = 1L;

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private EspacioRepository espacioRepository;

    private ReservaService servicio;

    @BeforeEach
    void preparar() {
        servicio = new ReservaService(
            reservaRepository,
            Clock.systemDefaultZone(),
            usuarioRepository,
            espacioRepository
        );
    }

    //

    @Test
    void crearReservaValidaSeGuardaPendiente() {
        LocalDate fecha = LocalDate.now().plusDays(7);
        LocalTime inicio = LocalTime.of(10, 0);
        LocalTime fin = LocalTime.of(11, 0);

        Usuario usuario = new Usuario();
        Espacio espacio = new Espacio(
            "Sala de estudio", "SALA", 20, true
        );

        when(usuarioRepository.findById(USUARIO_ID))
            .thenReturn(usuario);

        when(espacioRepository.findById(ESPACIO_ID))
            .thenReturn(espacio);

        Reserva reserva = servicio.crearReserva(
            USUARIO_ID,
            ESPACIO_ID,
            fecha,
            inicio,
            fin,
            "Reunión de estudio"
        );

        assertNotNull(reserva);
        assertEquals(EstadoReserva.PENDIENTE, reserva.getEstado());
        assertEquals(usuario, reserva.getUsuario());
        assertEquals(espacio, reserva.getEspacio());

        verify(reservaRepository).guardar(reserva);
    }


    @Test
    void crearReservaConHorarioInvalidoLanzaError() {
        LocalDate fecha = LocalDate.now().plusDays(7);
        LocalTime inicio = LocalTime.of(12, 0);
        LocalTime fin = LocalTime.of(10, 0);

        assertThrows(ReservaException.class, () -> servicio.crearReserva(
            USUARIO_ID,
            ESPACIO_ID,
            fecha,
            inicio,
            fin,
            "Reunión de estudio"
            )
        );

        verify(reservaRepository, never()).guardar(any());
    }

    @Test
    void crearReservaConConflictoHorarioLanzaError() {
        LocalDate fecha = LocalDate.now().plusDays(7);
        LocalTime inicio = LocalTime.of(10, 0);
        LocalTime fin = LocalTime.of(11, 0);

        Usuario usuario = new Usuario();
        Espacio espacio = new Espacio(
            "Sala de estudio", "SALA", 20, true
        );

        when(usuarioRepository.findById(USUARIO_ID))
            .thenReturn(usuario);

        when(espacioRepository.findById(ESPACIO_ID))
            .thenReturn(espacio);

        when(reservaRepository.existeConflicto(
            ESPACIO_ID, fecha, inicio, fin
        )).thenReturn(true);

        assertThrows(ReservaException.class, () -> servicio.crearReserva(
                USUARIO_ID,
                ESPACIO_ID,
                fecha,
                inicio,
                fin,
                "Reunión de estudio"
            )
        );

        verify(reservaRepository, never()).guardar(any());
    }


    @Test
    void crearReservaConUsuarioInexistenteLanzaError() {
        LocalDate fecha = LocalDate.now().plusDays(7);
        LocalTime inicio = LocalTime.of(10, 0);
        LocalTime fin = LocalTime.of(11, 0);

        when(usuarioRepository.findById(USUARIO_ID))
            .thenReturn(null);

        assertThrows(ReservaException.class, () ->servicio.crearReserva(
                USUARIO_ID,
                ESPACIO_ID,
                fecha,
                inicio,
                fin,
                "Reunión de estudio"
                )
            );

        verify(reservaRepository, never()).guardar(any());
    }

    @Test
    void crearReservaConSalaNoDisponibleLanzaError() {
        LocalDate fecha = LocalDate.now().plusDays(7);
        LocalTime inicio = LocalTime.of(10, 0);
        LocalTime fin = LocalTime.of(11, 0);

        Usuario usuario = new Usuario();
        Espacio espacio = new Espacio(
            "Sala de estudio", "SALA", 20, false
        );

        when(usuarioRepository.findById(USUARIO_ID))
            .thenReturn(usuario);

        when(espacioRepository.findById(ESPACIO_ID))
            .thenReturn(espacio);

        assertThrows(ReservaException.class, () ->
            servicio.crearReserva(
                USUARIO_ID,
                ESPACIO_ID,
                fecha,
                inicio,
                fin,
                "Reunión de estudio"
            )
        );

        verify(reservaRepository, never()).guardar(any());
    }



    @Test
    void crearReservaConFechaPasadaLanzaError() {
        LocalDate fecha = LocalDate.now().minusDays(1);
        LocalTime inicio = LocalTime.of(10, 0);
        LocalTime fin = LocalTime.of(11, 0);

        assertThrows(ReservaException.class, () ->
            servicio.crearReserva(
                USUARIO_ID,
                ESPACIO_ID,
                fecha,
                inicio,
                fin,
                "Reunión de estudio"
            )
        );

        verify(reservaRepository, never()).guardar(any());
    }

    @Test
    void crearReservaConSalaInexistenteLanzaError() {
        LocalDate fecha = LocalDate.now().plusDays(7);
        LocalTime inicio = LocalTime.of(10, 0);
        LocalTime fin = LocalTime.of(11, 0);

        Usuario usuario = new Usuario();

        when(usuarioRepository.findById(USUARIO_ID))
            .thenReturn(usuario);

        when(espacioRepository.findById(ESPACIO_ID))
            .thenReturn(null);

        assertThrows(ReservaException.class, () ->
            servicio.crearReserva(
                USUARIO_ID,
                ESPACIO_ID,
                fecha,
                inicio,
                fin,
                "Reunión de estudio"
            )
        );

        verify(reservaRepository, never()).guardar(any());
    }

    @Test
    void crearReservaSinMotivoLanzaError() {
        LocalDate fecha = LocalDate.now().plusDays(7);
        LocalTime inicio = LocalTime.of(10, 0);
        LocalTime fin = LocalTime.of(11, 0);

        assertThrows(ReservaException.class, () ->
            servicio.crearReserva(
                USUARIO_ID,
                ESPACIO_ID,
                fecha,
                inicio,
                fin,
                ""
            )
        );

        verify(reservaRepository, never()).guardar(any());
    }


    @Test
    void crearReservaSinFechaLanzaError() {
        LocalTime inicio = LocalTime.of(10, 0);
        LocalTime fin = LocalTime.of(11, 0);

        assertThrows(ReservaException.class, () ->
            servicio.crearReserva(
                USUARIO_ID, ESPACIO_ID, null,
                inicio, fin, "Reunión de estudio"
            )
        );

        verify(reservaRepository, never()).guardar(any());
    }

    @Test
    void crearReservaSinUsuarioIdLanzaError() {
        LocalDate fecha = LocalDate.now().plusDays(7);
        LocalTime inicio = LocalTime.of(10, 0);
        LocalTime fin = LocalTime.of(11, 0);

        assertThrows(ReservaException.class, () ->
            servicio.crearReserva(
                null, ESPACIO_ID, fecha,
                inicio, fin, "Reunión de estudio"
            )
        );

        verify(reservaRepository, never()).guardar(any());
    }

    @Test
    void crearReservaSinEspacioIdLanzaError() {
        LocalDate fecha = LocalDate.now().plusDays(7);
        LocalTime inicio = LocalTime.of(10, 0);
        LocalTime fin = LocalTime.of(11, 0);

        assertThrows(ReservaException.class, () ->
            servicio.crearReserva(
                USUARIO_ID, null, fecha,
                inicio, fin, "Reunión de estudio"
            )
        );

        verify(reservaRepository, never()).guardar(any());
    }

    @Test
    void crearReservaSinHoraInicioLanzaError() {
        LocalDate fecha = LocalDate.now().plusDays(7);
        LocalTime fin = LocalTime.of(11, 0);

        assertThrows(ReservaException.class, () ->
            servicio.crearReserva(
                USUARIO_ID, ESPACIO_ID, fecha,
                null, fin, "Reunión de estudio"
            )
        );

        verify(reservaRepository, never()).guardar(any());
    }

    @Test
    void crearReservaSinHoraFinLanzaError() {
        LocalDate fecha = LocalDate.now().plusDays(7);
        LocalTime inicio = LocalTime.of(10, 0);

        assertThrows(ReservaException.class, () ->
            servicio.crearReserva(
                USUARIO_ID, ESPACIO_ID, fecha,
                inicio, null, "Reunión de estudio"
            )
        );

        verify(reservaRepository, never()).guardar(any());
    }

    @Test
    void crearReservaConMotivoEnBlancoLanzaError() {
        LocalDate fecha = LocalDate.now().plusDays(7);
        LocalTime inicio = LocalTime.of(10, 0);
        LocalTime fin = LocalTime.of(11, 0);

        assertThrows(ReservaException.class, () ->
            servicio.crearReserva(
                USUARIO_ID, ESPACIO_ID, fecha,
                inicio, fin, "   "
            )
        );

        verify(reservaRepository, never()).guardar(any());
    }






}
