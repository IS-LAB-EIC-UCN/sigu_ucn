package cl.ucn.app.controller.biblioteca;

import cl.ucn.app.model.biblioteca.Multa;
import cl.ucn.app.repository.biblioteca.MultaRepository;
import cl.ucn.app.service.biblioteca.MultaService;
import io.javalin.http.Context;

public class MultaController {

    private final MultaService multaService = new MultaService();
    private final MultaRepository multaRepository = new MultaRepository();

    public void registrarPago(Context ctx) {
        String multaIdStr = ctx.formParam("multaId");
        if (multaIdStr == null || multaIdStr.isBlank()) {
            ctx.redirect(returnTo(ctx));
            return;
        }

        Long multaId = Long.parseLong(multaIdStr);
        Multa multa = multaRepository.findById(multaId);
        if (multa == null || multa.getPagada()) {
            ctx.redirect(returnTo(ctx));
            return;
        }
        multaService.registrarPago(multaId);
        ctx.redirect(returnTo(ctx));
    }

    private String returnTo(Context ctx) {
        String returnTo = ctx.formParam("returnTo");
        if (returnTo == null || returnTo.isBlank() || !returnTo.startsWith("/biblioteca/")) {
            return "/biblioteca/historial";
        }
        return returnTo;
    }
}
