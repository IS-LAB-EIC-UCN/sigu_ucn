package cl.ucn.app.controller;

import cl.ucn.app.dto.TutoriaFiltro;
import cl.ucn.app.service.TutoriaBusquedaService;
import cl.ucn.app.repository.AsignaturaRepository;
import cl.ucn.app.repository.TutorRepository;
import cl.ucn.app.util.VistaUtil;
import io.javalin.http.Context;

import java.time.LocalDate;
import java.util.Map;

public class TutoriaController {

    private final TutoriaBusquedaService busquedaService;
    private final AsignaturaRepository asignaturaRepository;
    private final TutorRepository tutorRepository;

    public TutoriaController(TutoriaBusquedaService busquedaService,
                             AsignaturaRepository asignaturaRepository,
                             TutorRepository tutorRepository) {
        this.busquedaService = busquedaService;
        this.asignaturaRepository = asignaturaRepository;
        this.tutorRepository = tutorRepository;
    }

    public void listar(Context ctx) {
        TutoriaFiltro filtro = new TutoriaFiltro(
                parseLong(ctx.queryParam("tutorId")),
                parseLong(ctx.queryParam("asignaturaId")),
                parseDate(ctx.queryParam("fecha"))
        );
        Map<String, Object> model = VistaUtil.modeloBase(ctx, "Tutorías - SIGU-UCN");
        model.put("tutorias", busquedaService.buscar(filtro));
        model.put("filtro", filtro);
        model.put("asignaturas", asignaturaRepository.findAll());
        model.put("tutores", tutorRepository.findAll());
        ctx.render("tutorias/listado.jte", model);
    }

    private Long parseLong(String value) {
        try {
            return value == null || value.isBlank() ? null : Long.parseLong(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private LocalDate parseDate(String value) {
        try {
            return value == null || value.isBlank() ? null : LocalDate.parse(value);
        } catch (java.time.format.DateTimeParseException e) {
            return null;
        }
    }
}
