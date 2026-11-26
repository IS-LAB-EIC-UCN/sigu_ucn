package cl.ucn.app.controller;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import cl.ucn.app.model.Estacionamiento;
import cl.ucn.app.service.EstacionamientoService;
import cl.ucn.app.service.EstacionamientoService.AccesoDenegadoException;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AdminEstacionamientoControllerTest {

    private static final String RUTA = "/estacionamientos/admin";
    private static final String ATRIBUTO_AVISO = "adminEstacionamientoAviso";

    private EstacionamientoService service;
    private Context ctx;
    private AdminEstacionamientoController controller;

    @BeforeEach
    void preparar() {
        service = mock(EstacionamientoService.class);
        ctx = mock(Context.class);
        controller = new AdminEstacionamientoController(service);
    }

    @Test
    void redirigeLasTresOperacionesSinSesionAlLogin() {
        controller.mostrarCatalogo(ctx);
        controller.registrar(ctx);
        controller.cambiarHabilitacion(ctx);

        verify(ctx, times(3)).redirect("/login");
        verifyNoInteractions(service);
    }

    @Test
    void rechazaElListadoYAmbosPostSiElServicioDeniegaElAcceso() {
        when(ctx.sessionAttribute("usuarioId")).thenReturn(1L);
        when(ctx.sessionAttribute("usuarioRol")).thenReturn("ADMIN");
        when(ctx.status(403)).thenReturn(ctx);
        when(service.listar(1L)).thenThrow(new AccesoDenegadoException());
        when(service.registrar(1L, null, null, null)).thenThrow(new AccesoDenegadoException());
        when(service.cambiarHabilitacion(1L, null, null)).thenThrow(new AccesoDenegadoException());

        controller.mostrarCatalogo(ctx);
        controller.registrar(ctx);
        controller.cambiarHabilitacion(ctx);

        verify(ctx, times(3)).status(403);
        verify(ctx, never()).redirect(anyString(), any(HttpStatus.class));
    }

    @Test
    void muestraConfirmacionDeAltaConElCodigoNormalizado() {
        when(ctx.sessionAttribute("usuarioId")).thenReturn(1L);
        when(ctx.formParam("codigo")).thenReturn(" c01 ");
        when(ctx.formParam("tipo")).thenReturn("COMUN");
        when(ctx.formParam("habilitado")).thenReturn("true");
        var espacio = new Estacionamiento("C01", true);
        when(service.registrar(1L, " c01 ", "COMUN", "true")).thenReturn(espacio);
        var catalogo = List.of(espacio);
        when(service.listar(1L)).thenReturn(catalogo);

        controller.registrar(ctx);
        cargarAviso();
        controller.mostrarCatalogo(ctx);

        verify(ctx).render(eq("estacionamientos/admin.jte"), argThat(modelo ->
                catalogo.equals(modelo.get("estacionamientos"))
                        && "El estacionamiento C01 se registró correctamente.".equals(modelo.get("mensaje"))
                        && "".equals(modelo.get("error"))));
        verify(ctx).sessionAttribute(ATRIBUTO_AVISO, null);
    }

    @Test
    void muestraElErrorEspecificoAlVolverAlCatalogo() {
        when(ctx.sessionAttribute("usuarioId")).thenReturn(1L);
        when(ctx.pathParam("id")).thenReturn("7");
        when(ctx.formParam("habilitado")).thenReturn("false");
        String mensaje = "El estacionamiento tiene reservas pendientes o aprobadas que aún no terminan.";
        when(service.cambiarHabilitacion(1L, 7L, "false")).thenThrow(new IllegalArgumentException(mensaje));
        when(service.listar(1L)).thenReturn(List.of());

        controller.cambiarHabilitacion(ctx);
        cargarAviso();
        controller.mostrarCatalogo(ctx);

        verify(ctx).render(eq("estacionamientos/admin.jte"), argThat(modelo ->
                mensaje.equals(modelo.get("error")) && "".equals(modelo.get("mensaje"))));
    }

    private void cargarAviso() {
        verify(ctx).redirect(RUTA, HttpStatus.SEE_OTHER);
        var aviso = ArgumentCaptor.forClass(Object.class);
        verify(ctx).sessionAttribute(eq(ATRIBUTO_AVISO), aviso.capture());
        when(ctx.sessionAttribute(ATRIBUTO_AVISO)).thenReturn(aviso.getValue());
    }
}
