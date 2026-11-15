package cl.ucn.app.controller;

import cl.ucn.app.service.AgendaService;
import cl.ucn.app.util.SessionUtil;
import cl.ucn.app.util.VistaUtil;
import io.javalin.http.Context;

import java.util.Map;

public class AgendaController {

    private final AgendaService agendaService;

    public AgendaController(AgendaService agendaService) {
        this.agendaService = agendaService;
    }

    public void mostrar(Context ctx) {
        Map<String, Object> model = VistaUtil.modeloBase(ctx, "Agenda - SIGU-UCN");
        boolean tienePerfilEstudiante = "ESTUDIANTE".equals(SessionUtil.usuarioRol(ctx))
                || SessionUtil.esTutor(ctx);
        model.put("reservas", tienePerfilEstudiante
                ? agendaService.agendaEstudiante(SessionUtil.usuarioId(ctx))
                : java.util.List.of());
        model.put("tutorias", SessionUtil.esTutor(ctx)
                ? agendaService.agendaTutor(SessionUtil.tutorId(ctx))
                : java.util.List.of());
        ctx.render("tutorias/agenda.jte", model);
    }
}
