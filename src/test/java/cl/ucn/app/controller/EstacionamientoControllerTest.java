package cl.ucn.app.controller;

import cl.ucn.app.service.ReservaEstacionamientoService;
import io.javalin.http.Context;
import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EstacionamientoControllerTest {
    private final Context ctx = mock(Context.class);
    private final ReservaEstacionamientoService service = mock(ReservaEstacionamientoService.class);
    private final EstacionamientoController controller = new EstacionamientoController(service);

    @BeforeEach
    void setUp() {
        when(ctx.sessionAttribute("usuarioId")).thenReturn(1L);
        when(ctx.formParam("vehiculoId")).thenReturn("2");
        when(ctx.formParam("estacionamientoId")).thenReturn("3");
        when(ctx.formParam("fechaHoraInicio")).thenReturn("2030-01-01T10:00");
        when(ctx.formParam("fechaHoraFin")).thenReturn("2030-01-01T11:00");
    }

    @Test
    void comunicaConflictoConMensajeDelServicio() {
        when(service.registrarReserva(any(), any(), any(), any(), any()))
                .thenThrow(new IllegalArgumentException("El vehículo ya tiene una reserva en ese horario."));
        controller.registrarReserva(ctx);
        verify(ctx).sessionAttribute("reservaError", "El vehículo ya tiene una reserva en ese horario.");
        verify(ctx).redirect("/estacionamientos/vehiculos/nuevo?errorReserva=1");
    }

    @Test
    void ocultaDetalleDePersistencia() {
        when(service.registrarReserva(any(), any(), any(), any(), any()))
                .thenThrow(new PersistenceException("Detalle SQL interno"));
        controller.registrarReserva(ctx);
        verify(ctx).sessionAttribute("reservaError", "No se pudo guardar la reserva. Inténtalo nuevamente.");
    }

    @Test
    void controlaFechaMalFormada() {
        when(ctx.formParam("fechaHoraInicio")).thenReturn("fecha inválida");
        controller.registrarReserva(ctx);
        verifyNoInteractions(service);
        verify(ctx).sessionAttribute("reservaError", "Revisa el vehículo, el estacionamiento y las fechas de la reserva.");
    }

    @Test
    void redirigeAlLoginSinSesion() {
        when(ctx.sessionAttribute("usuarioId")).thenReturn(null);
        controller.registrarReserva(ctx);
        verify(ctx).redirect("/login");
        verifyNoInteractions(service);
    }

    @Test
    void confirmaSoloDespuesDeGuardar() {
        controller.registrarReserva(ctx);
        verify(service).registrarReserva(any(), any(), any(), any(), any());
        verify(ctx).redirect("/estacionamientos/vehiculos/nuevo?reserva=1");
        verify(ctx, never()).sessionAttribute(eq("reservaError"), any());
    }
}
