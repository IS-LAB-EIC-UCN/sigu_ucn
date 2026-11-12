package cl.ucn.app.controller;
import cl.ucn.app.repository.*;
import io.javalin.http.Context;
import org.junit.jupiter.api.*;
import static org.mockito.Mockito.*;
class InscripcionesControllerTest {
    private final InscripcionRepository repo=mock(InscripcionRepository.class);
    private final Context ctx=mock(Context.class,RETURNS_SELF);
    private final InscripcionesController controller=new InscripcionesController(repo,mock(EventoRepository.class));
    @BeforeEach void preparar() {
        when(ctx.sessionAttribute("usuarioRol")).thenReturn("ESTUDIANTE");
        when(ctx.sessionAttribute("usuarioId")).thenReturn(3L);
        when(ctx.sessionAttribute("usuarioNombre")).thenReturn("Participante");
        when(ctx.sessionAttribute("csrfToken")).thenReturn("token");
        when(ctx.formParam("csrfToken")).thenReturn("token");when(ctx.pathParam("id")).thenReturn("15");
    }
    @Test void confirmaSoloDespuesDePersistirLaInscripcion() {
        controller.inscribir(ctx);
        var orden=inOrder(repo,ctx);orden.verify(repo).inscribir(15L,3L);
        orden.verify(ctx).sessionAttribute("eventosAviso","Tu inscripción se registró correctamente. Ya tienes un cupo reservado.");
        orden.verify(ctx).redirect("/eventos");
    }
    @Test void noConfirmaCuandoNoQuedanCupos() {
        doThrow(new IllegalStateException("No quedan cupos")).when(repo).inscribir(15L,3L);
        controller.inscribir(ctx);verify(ctx).status(409);
        verify(ctx,never()).sessionAttribute(eq("eventosAviso"),any());verify(ctx,never()).redirect("/eventos");
    }
    @Test void rechazaInscripcionYCancelacionDeRolesNoParticipantes() {
        for (String rol : new String[]{"DOCENTE", "ADMIN", "OTRO"}) {
            when(ctx.sessionAttribute("usuarioRol")).thenReturn(rol);
            controller.inscribir(ctx);
            controller.cancelar(ctx);
        }
        when(ctx.sessionAttribute("usuarioRol")).thenReturn(null);
        controller.inscribir(ctx);
        controller.cancelar(ctx);
        verify(ctx,times(8)).status(403);
        verifyNoInteractions(repo);
        verify(ctx,never()).redirect(anyString());
    }
    @Test void confirmaCancelacion() {
        controller.cancelar(ctx);verify(repo).cancelarInscripcion(15L,3L);
        verify(ctx).sessionAttribute("eventosAviso","Tu inscripción fue cancelada. El cupo quedó disponible.");
    }
}
