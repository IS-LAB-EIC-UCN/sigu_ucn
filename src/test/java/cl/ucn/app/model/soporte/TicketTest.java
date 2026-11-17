package cl.ucn.app.model.soporte;

import cl.ucn.app.model.Rol;
import cl.ucn.app.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TicketTest {

    private Usuario solicitante;
    private Usuario admin;
    private Usuario tecnico;
    private Usuario otroTecnico;
    private Ticket ticket;

    @BeforeEach
    void setUp() {
        solicitante = usuario("Estudiante Demo", "ESTUDIANTE");
        admin = usuario("Administrador General", "ADMIN");
        tecnico = usuario("Tecnico Demo", "TECNICO");
        otroTecnico = usuario("Otro Tecnico", "TECNICO");
        ticket = new Ticket("No funciona wifi", "Sin conexión en sala A101", solicitante);
    }

    @Test
    void nuevoTicket_quedaCreadoYRegistraHistorial() {
        assertEquals(EstadoTicket.CREADO, ticket.getEstado());
        assertEquals(1, ticket.getHistorial().size());

        HistorialTicket entrada = ticket.getHistorial().get(0);
        assertEquals("Ticket creado", entrada.getAccionRealizada());
        assertNull(entrada.getEstadoAnterior());
        assertEquals(EstadoTicket.CREADO, entrada.getEstadoNuevo());
        assertSame(solicitante, entrada.getAutor());
    }

    @Test
    void asignarTecnico_pasaAAsignadoYRegistraHistorialConAutorAdmin() {
        ticket.asignarTecnico(tecnico, admin);

        assertEquals(EstadoTicket.ASIGNADO, ticket.getEstado());
        HistorialTicket entrada = ultima(ticket.getHistorial());
        assertSame(admin, entrada.getAutor());
        assertEquals(EstadoTicket.CREADO, entrada.getEstadoAnterior());
        assertEquals(EstadoTicket.ASIGNADO, entrada.getEstadoNuevo());
        assertEquals("Técnico asignado: Tecnico Demo", entrada.getAccionRealizada());
    }

    @Test
    void iniciarResolucion_porOtroTecnico_lanzaSecurityException() {
        ticket.asignarTecnico(tecnico, admin);

        assertThrows(SecurityException.class, () -> ticket.iniciarResolucion(otroTecnico));
        assertEquals(EstadoTicket.ASIGNADO, ticket.getEstado());
    }

    @Test
    void cerrar_sinResolucion_lanzaIllegalState() {
        dejarEnResolucion();

        assertThrows(IllegalStateException.class, () -> ticket.cerrar(tecnico));
        assertEquals(EstadoTicket.EN_RESOLUCION, ticket.getEstado());
    }

    @Test
    void cerrar_conResolucion_quedaCerradoConFechaEHistorial() {
        dejarEnResolucion();

        ticket.registrarResolucion("Se reinició el router", tecnico);
        ticket.cerrar(tecnico);

        assertEquals(EstadoTicket.CERRADO, ticket.getEstado());
        assertNotNull(ticket.getFechaCierre());
        assertEquals("Se reinició el router", ticket.getResolucion());
        HistorialTicket entrada = ultima(ticket.getHistorial());
        assertEquals("Ticket cerrado", entrada.getAccionRealizada());
        assertEquals(EstadoTicket.EN_RESOLUCION, entrada.getEstadoAnterior());
        assertEquals(EstadoTicket.CERRADO, entrada.getEstadoNuevo());
    }

    @Test
    void ticketCerrado_noAdmiteNuevaAsignacion() {
        dejarEnResolucion();
        ticket.registrarResolucion("Se reinició el router", tecnico);
        ticket.cerrar(tecnico);

        assertThrows(IllegalStateException.class, () -> ticket.asignarTecnico(tecnico, admin));
    }

    @Test
    void cerrar_porOtroTecnico_lanzaSecurityException() {
        dejarEnResolucion();
        ticket.registrarResolucion("Se reinició el router", tecnico);

        assertThrows(SecurityException.class, () -> ticket.cerrar(otroTecnico));
    }

    private void dejarEnResolucion() {
        ticket.asignarTecnico(tecnico, admin);
        ticket.iniciarResolucion(tecnico);
    }

    private static Usuario usuario(String nombre, String rol) {
        return new Usuario(nombre, nombre.replace(' ', '.') + "@sigu.cl", "clave", true, new Rol(rol));
    }

    private static HistorialTicket ultima(List<HistorialTicket> historial) {
        return historial.get(historial.size() - 1);
    }
}
