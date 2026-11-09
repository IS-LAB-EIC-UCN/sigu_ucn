
package cl.ucn.app.controller;

import cl.ucn.app.model.Evento;
import cl.ucn.app.repository.EventoRepository;
import io.javalin.http.Context;

import java.util.List;
import java.util.Map;

public class EventosController {

    private final EventoRepository eventoRepository = new EventoRepository();

    public void mostrarEventos(Context ctx) {

        String usuarioNombre = ctx.sessionAttribute("usuarioNombre");

        if (usuarioNombre == null) {
            ctx.redirect("/login");
            return;
        }

        List<Evento> eventos = eventoRepository.findAll();

        ctx.render("eventos.jte", Map.of(
                "title", "Gestión de Eventos",
                "usuarioNombre", usuarioNombre,
                "eventos", eventos));
    }
}
