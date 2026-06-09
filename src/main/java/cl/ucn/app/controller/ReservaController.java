package cl.ucn.app.controller;

import cl.ucn.app.model.Reserva;
import cl.ucn.app.service.ReservaService;
import io.javalin.http.Context;
import java.time.LocalDateTime;
import java.util.Map;

public class ReservaController {
    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    public void create(Context ctx) {
        Map<String, String> body = ctx.bodyAsClass(Map.class);
        try {
            Reserva reserva = reservaService.crearReserva(
                body.get("usuarioId"),
                body.get("espacioId"),
                LocalDateTime.parse(body.get("fechaInicio")),
                LocalDateTime.parse(body.get("fechaFin"))
            );
            ctx.status(201).json(reserva);
        } catch (Exception e) {
            ctx.status(400).result(e.getMessage());
        }
    }

    public void getAll(Context ctx) {
        String usuarioId = ctx.queryParam("usuarioId");
        String espacioId = ctx.queryParam("espacioId");
        String estado = ctx.queryParam("estado");
        String desdeStr = ctx.queryParam("desde");
        String hastaStr = ctx.queryParam("hasta");

        LocalDateTime desde = (desdeStr != null) ? LocalDateTime.parse(desdeStr) : null;
        LocalDateTime hasta = (hastaStr != null) ? LocalDateTime.parse(hastaStr) : null;

        ctx.json(reservaService.buscarConFiltros(usuarioId, espacioId, estado, desde, hasta));
    }

    public void updateStatus(Context ctx) {
        String id = ctx.pathParam("id");
        String estadoParam = ctx.queryParam("estado");
        
        if (estadoParam == null) {
            ctx.status(400).result("Debe proporcionar el parámetro 'estado'");
            return;
        }

        try {
            Reserva.Estado nuevoEstado = Reserva.Estado.valueOf(estadoParam.toUpperCase());
            Reserva actualizada = reservaService.actualizarEstado(id, nuevoEstado);
            ctx.status(200).json(actualizada); // Ahora devuelve el objeto actualizado
        } catch (IllegalArgumentException e) {
            ctx.status(400).result("Estado inválido o reserva no encontrada");
        } catch (Exception e) {
            ctx.status(500).result("Error interno: " + e.getMessage());
        }
    }
}
