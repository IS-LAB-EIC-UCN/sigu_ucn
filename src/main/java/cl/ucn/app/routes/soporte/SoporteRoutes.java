package cl.ucn.app.routes.soporte;

import cl.ucn.app.controller.soporte.SoporteController;
import io.javalin.config.JavalinConfig;

public class SoporteRoutes {
    private SoporteRoutes(){}

    public static void register(JavalinConfig config) {
        SoporteController controller = new SoporteController();
        config.routes.get("/soporte", controller::mostrarInicio);
        config.routes.post("/api/soporte/tickets", controller::crearTicket);
        config.routes.get("/api/soporte/tecnicos", controller::listarTecnicos);
        config.routes.get("/api/soporte/tickets/pendientes", controller::listarPendientes);
        config.routes.put("/api/soporte/tickets/{id}/asignacion", controller::asignarTecnico);
        config.routes.get("/api/soporte/tickets/asignados", controller::listarAsignados);
        config.routes.put("/api/soporte/tickets/{id}/resolucion", controller::iniciarResolucion);
    }
}
