package cl.ucn.app.service;

import cl.ucn.app.model.CategoriaTicket;
import cl.ucn.app.model.Comentario;
import cl.ucn.app.model.Ticket;
import cl.ucn.app.model.Usuario;
import cl.ucn.app.repository.ICategoriaTicketRepository;
import cl.ucn.app.repository.IComentarioRepository;
import cl.ucn.app.repository.ITicketRepository;
import cl.ucn.app.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para TicketServiceImpl.
 * Cumple con el requisito de pruebas unitarias (8-12 pruebas).
 */
@ExtendWith(MockitoExtension.class)
class TicketServiceImplTest {

    @Mock
    private ITicketRepository ticketRepository;

    @Mock
    private IComentarioRepository comentarioRepository;

    @Mock
    private ICategoriaTicketRepository categoriaTicketRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    private TicketServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new TicketServiceImpl(ticketRepository, comentarioRepository, categoriaTicketRepository, usuarioRepository);
    }

    private Ticket ticketAbierto() {
        Ticket t = new Ticket();
        t.setId(1L);
        t.setEstado("ABIERTO");
        return t;
    }

    private Usuario usuarioMock(Long id) {
        Usuario u = new Usuario();
        u.setId(id);
        u.setNombre("Usuario " + id);
        return u;
    }

    @Test
    void crearTicket_conDatosValidos_creaTicketAbierto() {
        CategoriaTicket cat = new CategoriaTicket("Redes", "desc");
        cat.setId(1L);
        when(categoriaTicketRepository.buscarPorId(1L)).thenReturn(Optional.of(cat));
        when(usuarioRepository.findById(10L)).thenReturn(Optional.of(usuarioMock(10L)));

        Ticket t = service.crearTicket("Falla VPN", "No conecta", "ALTA", 1L, 10L);

        assertNotNull(t);
        assertEquals("ABIERTO", t.getEstado());
        assertEquals("ALTA", t.getPrioridad());
        verify(ticketRepository, times(1)).guardar(any(Ticket.class));
    }

    @Test
    void crearTicket_sinTitulo_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> service.crearTicket("", "desc", "MEDIA", 1L, 10L));
        verifyNoInteractions(ticketRepository);
    }

    @Test
    void crearTicket_prioridadInvalida_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> service.crearTicket("Titulo", "desc", "URGENTE", 1L, 10L));
    }

    @Test
    void crearTicket_categoriaInexistente_lanzaExcepcion() {
        when(categoriaTicketRepository.buscarPorId(99L)).thenReturn(Optional.empty());
        when(usuarioRepository.findById(10L)).thenReturn(Optional.of(usuarioMock(10L)));

        assertThrows(IllegalArgumentException.class,
                () -> service.crearTicket("Titulo", "desc", "BAJA", 99L, 10L));
    }

    @Test
    void cambiarEstado_ticketCerrado_lanzaExcepcion() {
        Ticket t = ticketAbierto();
        t.setEstado("CERRADO");
        when(ticketRepository.buscarPorId(1L)).thenReturn(Optional.of(t));

        assertThrows(IllegalStateException.class,
                () -> service.cambiarEstado(1L, "EN_PROCESO", 5L));
        verify(ticketRepository, never()).actualizar(any());
    }

    @Test
    void cambiarEstado_aEnProceso_porTecnicoNoAsignado_lanzaExcepcion() {
        Ticket t = ticketAbierto();
        Usuario tecnicoAsignado = new Usuario();
        tecnicoAsignado.setId(5L);
        t.setTecnico(tecnicoAsignado);
        when(ticketRepository.buscarPorId(1L)).thenReturn(Optional.of(t));

        assertThrows(IllegalStateException.class,
                () -> service.cambiarEstado(1L, "EN_PROCESO", 999L));
    }

    @Test
    void cambiarEstado_aEnProceso_porTecnicoAsignado_actualizaEstado() {
        Ticket t = ticketAbierto();
        Usuario tecnicoAsignado = new Usuario();
        tecnicoAsignado.setId(5L);
        t.setTecnico(tecnicoAsignado);
        when(ticketRepository.buscarPorId(1L)).thenReturn(Optional.of(t));

        Ticket actualizado = service.cambiarEstado(1L, "EN_PROCESO", 5L);

        assertEquals("EN_PROCESO", actualizado.getEstado());
        verify(ticketRepository).actualizar(t);
    }

    @Test
    void cerrarTicket_sinResolucion_lanzaExcepcion() {
        Ticket t = ticketAbierto();
        when(ticketRepository.buscarPorId(1L)).thenReturn(Optional.of(t));

        assertThrows(IllegalArgumentException.class,
                () -> service.cerrarTicket(1L, "  "));
        verify(ticketRepository, never()).actualizar(any());
    }

    @Test
    void cerrarTicket_conResolucion_cierraCorrectamente() {
        Ticket t = ticketAbierto();
        when(ticketRepository.buscarPorId(1L)).thenReturn(Optional.of(t));

        Ticket cerrado = service.cerrarTicket(1L, "Se reinició el router");

        assertEquals("CERRADO", cerrado.getEstado());
        assertNotNull(cerrado.getFechaCierre());
        verify(ticketRepository).actualizar(t);
    }

    @Test
    void agregarComentario_ticketCerrado_lanzaExcepcion() {
        Ticket t = ticketAbierto();
        t.setEstado("CERRADO");
        when(ticketRepository.buscarPorId(1L)).thenReturn(Optional.of(t));

        assertThrows(IllegalStateException.class,
                () -> service.agregarComentario(1L, "hola", 10L));
        verifyNoInteractions(comentarioRepository);
    }

    @Test
    void agregarComentario_valido_seGuardaCorrectamente() {
        Ticket t = ticketAbierto();
        when(ticketRepository.buscarPorId(1L)).thenReturn(Optional.of(t));
        when(usuarioRepository.findById(10L)).thenReturn(Optional.of(usuarioMock(10L)));

        Comentario c = service.agregarComentario(1L, "Probando solución", 10L);

        assertNotNull(c);
        assertEquals("Probando solución", c.getContenido());
        verify(comentarioRepository).guardar(any(Comentario.class));
    }

    @Test
    void obtenerTicket_inexistente_lanzaExcepcion() {
        when(ticketRepository.buscarPorId(123L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.obtenerTicket(123L));
    }

    @Test
    void filtrarTickets_delegaAlRepositorio() {
        when(ticketRepository.filtrar("ABIERTO", null, null)).thenReturn(List.of(ticketAbierto()));

        List<Ticket> resultado = service.filtrarTickets("ABIERTO", null, null);

        assertEquals(1, resultado.size());
        verify(ticketRepository).filtrar("ABIERTO", null, null);
    }
}