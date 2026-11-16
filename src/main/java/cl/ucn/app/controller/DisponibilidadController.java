package cl.ucn.app.controller;

import cl.ucn.app.repository.EspacioRepository;
import cl.ucn.app.service.Disponibilidad;
import cl.ucn.app.service.DisponibilidadService;
import cl.ucn.app.service.ReservaException;
import io.javalin.http.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DisponibilidadController {

    private static final Logger log = LoggerFactory.getLogger(DisponibilidadController.class);

    private final DisponibilidadService disponibilidadService;
    private final EspacioRepository espacioRepository;

    public DisponibilidadController(DisponibilidadService disponibilidadService,
                                    EspacioRepository espacioRepository) {
        this.disponibilidadService = Objects.requireNonNull(disponibilidadService);
        this.espacioRepository = Objects.requireNonNull(espacioRepository);
    }

    public void consultar(Context ctx) {
        Long usuarioId = ctx.sessionAttribute("usuarioId");
        String usuarioNombre = ctx.sessionAttribute("usuarioNombre");
        if (usuarioId == null) {
            ctx.redirect("/login");
            return;
        }

        String salaParam = ctx.queryParam("sala");
        String fechaParam = ctx.queryParam("fecha");

        Long salaId = null;
        LocalDate fecha = LocalDate.now();
        Disponibilidad disponibilidad = null;
        String error = "";
        boolean parametrosValidos = true;

        if (salaParam != null && !salaParam.isBlank()) {
            try {
                salaId = Long.valueOf(salaParam.trim());
            } catch (NumberFormatException e) {
                error = "Fecha o sala inválida.";
                parametrosValidos = false;
            }
        }

        if (fechaParam != null && !fechaParam.isBlank()) {
            try {
                fecha = LocalDate.parse(fechaParam.trim());
            } catch (DateTimeParseException e) {
                error = "Fecha o sala inválida.";
                parametrosValidos = false;
            }
        }

        if (parametrosValidos && salaId != null) {
            try {
                disponibilidad = disponibilidadService.consultar(salaId, fecha);
            } catch (ReservaException e) {
                error = e.getMessage();
            } catch (Exception e) {
                log.error("Error inesperado al consultar disponibilidad para sala={} y fecha={}", salaId, fecha, e);
                error = "No se pudo consultar la disponibilidad en este momento. Intente nuevamente.";
            }
        }

        Map<String, Object> model = new HashMap<>();
        model.put("title", "Disponibilidad de salas - SIGU-UCN");
        model.put("usuarioNombre", usuarioNombre);
        model.put("salas", espacioRepository.findDisponibles());
        model.put("salaId", salaId);
        model.put("fecha", fecha);
        model.put("disponibilidad", disponibilidad);
        model.put("error", error);

        ctx.render("reservas/disponibilidad.jte", model);
    }
}
