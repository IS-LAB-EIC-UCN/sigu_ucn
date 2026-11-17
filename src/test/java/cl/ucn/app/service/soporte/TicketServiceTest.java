package cl.ucn.app.service.soporte;

import cl.ucn.app.repository.UsuarioRepository;
import cl.ucn.app.repository.soporte.PermisosSoporteRepository;
import cl.ucn.app.repository.soporte.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private PermisosSoporteRepository permisosRepository;

    private TicketService service;

    @BeforeEach
    void setUp() {
        service = new TicketService(ticketRepository, usuarioRepository, permisosRepository);
    }

    @Test
    void crearTicket_sinTitulo_lanzaExcepcionYNoGuarda() {
        assertThrows(IllegalArgumentException.class, () -> service.crearTicket("  ", "desc", 3L));

        verify(ticketRepository, never()).guardar(any());
    }

    @Test
    void asignarTecnico_usuarioNoAdministrador_lanzaSecurityException() {
        when(permisosRepository.esAdministrador(3L)).thenReturn(false);

        assertThrows(SecurityException.class, () -> service.asignarTecnico(1L, 4L, 3L));
    }

    @Test
    void asignarTecnico_destinoSinRolTecnico_lanzaExcepcion() {
        when(permisosRepository.esAdministrador(1L)).thenReturn(true);
        when(permisosRepository.esTecnico(3L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> service.asignarTecnico(1L, 3L, 1L));

        verify(ticketRepository, never()).asignarTecnico(any(), any(), any());
    }

    @Test
    void cerrarTicket_resolucionEnBlanco_lanzaExcepcionYNoLlamaRepositorio() {
        when(permisosRepository.esTecnico(4L)).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> service.cerrarTicket(1L, 4L, "   "));

        verify(ticketRepository, never()).cerrarTicket(any(), any(), any());
    }
}
