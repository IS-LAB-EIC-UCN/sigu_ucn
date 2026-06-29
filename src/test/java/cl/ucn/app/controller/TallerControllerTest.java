package cl.ucn.app.controller;

import io.javalin.http.Context;
import org.junit.jupiter.api.Test;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static org.mockito.Mockito.*;

public class TallerControllerTest {

    @Test
    public void siUsuarioEsEstudiante_NoPuedeEliminarTaller() {
        Context ctx = mock(Context.class);
        
        when(ctx.sessionAttribute("usuarioRol")).thenReturn("ESTUDIANTE");

        TallerController.eliminarTaller(ctx);

        String expectedErrorUrl = "/talleres?error=" + URLEncoder.encode("Acceso denegado. Solo administradores pueden eliminar talleres.", StandardCharsets.UTF_8);
        
        verify(ctx).redirect(expectedErrorUrl);

        verify(ctx, never()).pathParam("id");
    }

    @Test
    public void siUsuarioEsDocente_NoPuedeCrearTaller() {
        Context ctx = mock(Context.class);
        
        when(ctx.sessionAttribute("usuarioRol")).thenReturn("DOCENTE");

        when(ctx.status(anyInt())).thenReturn(ctx);

        TallerController.registrarTaller(ctx);

        verify(ctx).status(403);
        verify(ctx).result("Acceso denegado: Solo los Administradores pueden crear talleres.");
    }
}
