package cl.ucn.app.controller.biblioteca;

import cl.ucn.app.auth.LectorSessionHelper;
import cl.ucn.app.repository.biblioteca.MultaRepository;
import cl.ucn.app.service.biblioteca.MultaService;
import io.javalin.http.Context;

import java.util.HashMap;
import java.util.Map;

public class MultaController {

    private final MultaService multaService = new MultaService();
    private final MultaRepository multaRepository = new MultaRepository();

    public void registrarPago(Context ctx) {
        String usuarioNombre = ctx.sessionAttribute("usuarioNombre");

        String multaIdStr = ctx.formParam("multaId");
        if (multaIdStr == null || multaIdStr.isBlank()) {
            ctx.redirect("/biblioteca/historial");
            return;
        }

        Long multaId = Long.parseLong(multaIdStr);
        cl.ucn.app.model.biblioteca.Multa multa = multaRepository.findById(multaId);
        if (multa == null || multa.getPagada()) {
            ctx.redirect("/biblioteca/historial");
            return;
        }
        multaService.registrarPago(multaId);
        ctx.redirect("/biblioteca/historial");
    }
}
