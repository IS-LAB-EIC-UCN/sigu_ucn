package cl.ucn.app.controller;

import io.javalin.http.Context;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import io.javalin.http.ForbiddenResponse;

class EventosControllerTest {
    private final EventosController controller = new EventosController();
    private final Context ctx = mock(Context.class, RETURNS_SELF);

    private void autenticar() {
        when(ctx.sessionAttribute("usuarioId")).thenReturn(1L);
        when(ctx.sessionAttribute("csrfToken")).thenReturn("token");
    }

    @Test void estudianteNoPuedeCrearEventosNiExpositores() {
        autenticar();when(ctx.sessionAttribute("usuarioRol")).thenReturn("ESTUDIANTE");
        when(ctx.formParam("csrfToken")).thenReturn("token");
        assertThrows(ForbiddenResponse.class, () -> controller.mostrarFormulario(ctx));
        assertThrows(ForbiddenResponse.class, () -> controller.crearEvento(ctx));
        assertThrows(ForbiddenResponse.class, () -> controller.guardarExpositor(ctx));
    }
    @Test void anonimoEsRedirigidoAntesDeConsultarEventos() {
        controller.mostrarEventos(ctx);
        verify(ctx).redirect("/login");
        verify(ctx, never()).status(403);
    }

    @Test void operacionesRechazanTokenAusente() {
        autenticar();
        controller.crearEvento(ctx);
        controller.actualizarEvento(ctx);
        controller.cancelarEvento(ctx);
        controller.publicarEvento(ctx);
        verify(ctx, times(4)).status(403);
    }

}
