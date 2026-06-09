package cl.ucn.app.service;

import cl.ucn.app.model.Espacio;
import cl.ucn.app.model.Reserva;
import cl.ucn.app.model.Usuario;
import cl.ucn.app.model.Rol;
import cl.ucn.app.repository.EspacioRepository;
import cl.ucn.app.repository.ReservaRepository;
import cl.ucn.app.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class ReservaServiceTest {
    private ReservaRepository reservaRepository;
    private UsuarioRepository usuarioRepository;
    private EspacioRepository espacioRepository;
    private ReservaService reservaService;

    @BeforeEach
    public void setup() {
        reservaRepository = Mockito.mock(ReservaRepository.class);
        usuarioRepository = Mockito.mock(UsuarioRepository.class);
        espacioRepository = Mockito.mock(EspacioRepository.class);
        reservaService = new ReservaService(reservaRepository, usuarioRepository, espacioRepository);
    }

    @Test
    public void testCrearReservaValida() {
        LocalDateTime inicio = LocalDateTime.now().plusDays(1);
        LocalDateTime fin = inicio.plusHours(2);
        
        Usuario user = new Usuario("u1", "Test", "test@ucn.cl", "pass", Rol.ESTUDIANTE);
        Espacio space = new Espacio("e1", "Sala", "SALA", 10, true);

        when(usuarioRepository.findById("u1")).thenReturn(Optional.of(user));
        when(espacioRepository.findById("e1")).thenReturn(Optional.of(space));
        when(reservaRepository.hasOverlap(eq("e1"), any(), any())).thenReturn(false);

        Reserva reserva = reservaService.crearReserva("u1", "e1", inicio, fin);

        assertNotNull(reserva);
        assertEquals(Reserva.Estado.PENDIENTE, reserva.getEstado());
    }

    @Test
    public void testCrearReservaEnElPasadoLanzaExcepcion() {
        LocalDateTime inicio = LocalDateTime.now().minusDays(1);
        LocalDateTime fin = inicio.plusHours(2);

        assertThrows(IllegalArgumentException.class, () -> {
            reservaService.crearReserva("u1", "e1", inicio, fin);
        });
    }

    @Test
    public void testCrearReservaConSolapamientoLanzaExcepcion() {
        LocalDateTime inicio = LocalDateTime.now().plusDays(1);
        LocalDateTime fin = inicio.plusHours(2);
        
        Usuario user = new Usuario("u1", "Test", "test@ucn.cl", "pass", Rol.ESTUDIANTE);
        Espacio space = new Espacio("e1", "Sala", "SALA", 10, true);

        when(usuarioRepository.findById("u1")).thenReturn(Optional.of(user));
        when(espacioRepository.findById("e1")).thenReturn(Optional.of(space));
        when(reservaRepository.hasOverlap(eq("e1"), any(), any())).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> {
            reservaService.crearReserva("u1", "e1", inicio, fin);
        });
    }
}
