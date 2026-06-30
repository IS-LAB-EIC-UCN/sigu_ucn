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
                Long.parseLong(body.get("usuarioId")),
                Long.parseLong(body.get("espacioId")),
                LocalDateTime.parse(body.get("fechaInicio")),
                LocalDateTime.parse(body.get("fechaFin"))
            );
            ctx.status(201).json(reserva);
        } catch (Exception e) {
            ctx.status(400).result(e.getMessage());
        }
    }

    public void getAll(Context ctx) {
        String usuarioIdStr = ctx.queryParam("usuarioId");
        String espacioIdStr = ctx.queryParam("espacioId");
        String estado = ctx.queryParam("estado");
        String desdeStr = ctx.queryParam("desde");
        String hastaStr = ctx.queryParam("hasta");

        Long usuarioId = (usuarioIdStr != null && !usuarioIdStr.isEmpty()) ? Long.parseLong(usuarioIdStr) : null;
        Long espacioId = (espacioIdStr != null && !espacioIdStr.isEmpty()) ? Long.parseLong(espacioIdStr) : null;
        LocalDateTime desde = (desdeStr != null && !desdeStr.isEmpty()) ? LocalDateTime.parse(desdeStr) : null;
        LocalDateTime hasta = (hastaStr != null && !hastaStr.isEmpty()) ? LocalDateTime.parse(hastaStr) : null;

        ctx.json(reservaService.buscarConFiltros(usuarioId, espacioId, estado, desde, hasta));
    }

    public void updateStatus(Context ctx) {
        String idStr = ctx.pathParam("id");
        String estadoParam = ctx.queryParam("estado");
        
        if (estadoParam == null) {
            ctx.status(400).result("Debe proporcionar el parámetro 'estado'");
            return;
        }

        try {
            Long id = Long.parseLong(idStr);
            Reserva actualizada = reservaService.actualizarEstado(id, estadoParam.toUpperCase());
            ctx.status(200).json(actualizada);
        } catch (Exception e) {
            ctx.status(400).result("Error: " + e.getMessage());
        }
    }
}
