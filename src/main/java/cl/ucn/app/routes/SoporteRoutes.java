package cl.ucn.app.routes;

import cl.ucn.app.controller.TicketController;
import io.javalin.config.JavalinConfig;


public final class SoporteRoutes {

    private SoporteRoutes() {}

    public static void register(JavalinConfig config) {
        TicketController tc = new TicketController();

        config.routes.get("/soporte",                  tc::listarTickets);
        config.routes.get("/soporte/nuevo",            tc::mostrarFormularioCrear);
        config.routes.post("/soporte/nuevo",           tc::crearTicket);
        config.routes.get("/soporte/{id}",             tc::verTicket);
        config.routes.post("/soporte/{id}/asignar",    tc::asignarTecnico);
        config.routes.post("/soporte/{id}/estado",     tc::cambiarEstado);
        config.routes.post("/soporte/{id}/cerrar",     tc::cerrarTicket);
        config.routes.post("/soporte/{id}/comentar",   tc::agregarComentario);
    }
}