package cl.ucn.app.controller.biblioteca;

import cl.ucn.app.model.biblioteca.Multa;
import cl.ucn.app.repository.biblioteca.MultaRepository;
import cl.ucn.app.service.biblioteca.MultaService;
import io.javalin.http.Context;

import cl.ucn.app.service.biblioteca.api.IMultaService;
import cl.ucn.app.repository.biblioteca.api.IMultaRepository;

public class MultaController {

    private final IMultaService multaService;
    private final IMultaRepository multaRepository;

    public MultaController() {
        this(new MultaService(), new MultaRepository());
    }

    public MultaController(IMultaService multaService,
                           IMultaRepository multaRepository) {
        this.multaService = multaService;
        this.multaRepository = multaRepository;
    }

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
