package cl.ucn.app.routes;

import cl.ucn.app.controller.RegistroController;
import cl.ucn.app.repository.RolRepository;
import cl.ucn.app.repository.UsuarioRepository;
import cl.ucn.app.security.Pbkdf2PasswordHasher;
import cl.ucn.app.service.RegistroService;
import cl.ucn.app.util.AppProperties;
import io.javalin.config.JavalinConfig;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public final class RegistroRoutes {

    private RegistroRoutes() {
    }

    public static void register(JavalinConfig config) {
        RegistroService registroService = new RegistroService(
                new UsuarioRepository(),
                new RolRepository(),
                new Pbkdf2PasswordHasher(),
                dominiosPermitidos());
        RegistroController controller = new RegistroController(registroService);

        config.routes.get("/registro", controller::showRegistro);
        config.routes.post("/registro", controller::doRegistro);
    }

    /** Dominios de correo institucional definidos en application.properties. */
    private static Set<String> dominiosPermitidos() {
        String valor = AppProperties.get("registro.dominios.permitidos");
        if (valor == null || valor.isBlank()) {
            return Set.of();
        }
        return Arrays.stream(valor.split(","))
                .map(String::trim)
                .map(String::toLowerCase)
                .filter(d -> !d.isEmpty())
                .collect(Collectors.toSet());
    }
}
